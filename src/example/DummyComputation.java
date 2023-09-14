package example;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class DummyComputation extends BasicComputation<
        LongWritable, DoubleWritable, FloatWritable, DoubleWritable> {
    @Override
    public void compute(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex,
                        Iterable<DoubleWritable> messages) throws IOException {
        vertex.setValue(new DoubleWritable(1.0));
        vertex.voteToHalt();
    }
}
