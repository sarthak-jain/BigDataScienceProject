import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
 * Created by itawfik on 5/1/16.
 */
 import org.apache.spark.sql.expressions.MutableAggregationBuffer
 import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
 import org.apache.spark.sql.Row
 import org.apache.spark.sql.types._
 import org.apache.spark.sql.functions._

 class ArraySum extends UserDefinedAggregateFunction {
      def inputSchema: org.apache.spark.sql.types.StructType =
       StructType(StructField("value", ArrayType(IntegerType, false)) :: Nil)
  
     def bufferSchema: StructType =
       StructType(StructField("value", ArrayType(IntegerType, false)) :: Nil)
  
     def dataType: DataType = ArrayType(IntegerType, false)
  
     def deterministic: Boolean = true
  
     def initialize(buffer: MutableAggregationBuffer): Unit = {
         buffer(0) = Nil
       }
  
     def update(buffer: MutableAggregationBuffer,input: Row): Unit = {
         val currentSum : Seq[Int] = buffer.getSeq(0)
         val currentRow : Seq[Int] = input.getSeq(0)
         buffer(0) = (currentSum, currentRow) match {
           case (Nil, Nil) => Nil
               case (Nil, row) => row
               case (sum, Nil) => sum
               case (sum, row) => (sum, row).zipped.map{ case (a, b) => a + b }
             }
       }
  
     def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
         val currentSum : Seq[Int] = buffer1.getSeq(0)
         val currentRow : Seq[Int] = buffer2.getSeq(0)
         buffer1(0) = (currentSum, currentRow) match {
           case (Nil, Nil) => Nil
               case (Nil, row) => row
               case (sum, Nil) => sum
               case (sum, row) => (sum, row).zipped.map{ case (a, b) => a + b }
             }
       }
  
     def evaluate(buffer: Row): Any = {
         buffer.getSeq(0)
       }

}
