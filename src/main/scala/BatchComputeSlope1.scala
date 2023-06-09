import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.{SparkSession, _}
import org.apache.spark.sql.functions.{from_json, _}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.functions.split
import org.apache.spark.sql.types.{DoubleType, TimestampType}
import org.apache.spark.sql.types.{StructType, StructField, StringType, IntegerType}


object BatchComputeSlope1 {
  val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName("StreamingAndMl")
    .getOrCreate()

  def main(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._
    val df = spark.read.format("json").load("src/main/scala/testdata_1.json")
    // val schema = df.schema
    // df.printSchema()
    val schema = new StructType()
    .add("time", StringType)
    .add("temp", StringType)
    // val schema = new StructType()
    //   .add("time", StringType)
    //   .add(
    //     "data",
    //     new StructType()
    //       .add("degree", IntegerType)
    //   )

    // configure kafka
    val streamingData = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "BTC-Value")
      .load()

    val df1 = streamingData.selectExpr("CAST(value AS STRING)") // ,"timestamp")
    val rawData =
      df1.select(explode(split(df1.col("value"), "\n")).alias("value"))
    val df2 = rawData.select(
      from_json($"value".cast("string"), schema).alias("value")
    ) // , col("timestamp").alias("timestamp"))

    val parsed_json_data_with_schema = df2.select(
      col("value.time").alias("time"),
      col("value.temp").alias("temp")
    ) 
    
    //val query = parsed_json_data_with_schema.writeStream.outputMode("append").format("console").start()
    
    
    // ,
    // col("timestamp").alias("timestamp"))
    val dataFrame_for_Ml = parsed_json_data_with_schema
      .withColumn("time", col("time").cast(DoubleType))
      .withColumn("temp", col("temp").cast(DoubleType))
    // // .withColumn("timestamp", col("timestamp").cast(TimestampType))
    val vectorAssembler =
      new VectorAssembler().setInputCols(Array("time")).setOutputCol("features")
    // // val query = dataFrame_for_Ml.writeStream.format("console").outputMode("append").start()
    val query = dataFrame_for_Ml.writeStream
      // trigger(Trigger.ProcessingTime(10000))
      .foreachBatch((dataset: DataFrame, batchId: Long) => {
        if (dataset.isEmpty) {} else {
          val vecTrainDF = vectorAssembler.transform(dataset)
          val lr = new LinearRegression()
            .setFeaturesCol("features")
            .setLabelCol("temp")
          val lr_model = lr.fit(vecTrainDF)
          val slope = lr_model.coefficients(0)
          val round_slope = BigDecimal(slope).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
          val data_slope = Seq("BTC.Slope:" + round_slope)
          val output = data_slope.toDF()
          // output.write.format("csv").save("slope/batch_slope")
          output
            .selectExpr("CAST(value AS STRING)")
            .write
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("topic", "MyTest-BTC.Slope")
            .save()

        }
      })
      .start()

    query.awaitTermination()
  }
}
