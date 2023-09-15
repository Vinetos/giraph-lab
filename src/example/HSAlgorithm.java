package example;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.utils.ArrayListWritable;
import org.apache.hadoop.io.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class HSAlgorithm extends BasicComputation<LongWritable, DoubleWritable, FloatWritable, HSAlgorithm.Message> {

    @Override
    public void compute(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex, Iterable<HSAlgorithm.Message> messages) {
        boolean shouldSendMessage = true;

        // Reading vertex messages
        for (Message message : messages) {
            // This message if for me
            if (message.remainingHops.get() == 0) {
                // There is a bigger id, so I am no longer in the election process
                if (message.id.get() > vertex.getId().get()) {
                    // Sent back a reply
                    LongArrayListWritable path = message.path;
                    LongWritable dst = path.pop();
                    sendMessage(dst, new Message(message.id, path, Math.pow(2, getSuperstep()) - 1, true));
                    vertex.setValue(message.id); // Update the value with the temporary new leader
                } else if (message.id.get() == vertex.getId().get()) {
                    // I am the winner of this round
                    if(!message.isReply.get()) {
                        // Not a reply => the receiver is the source, then the election is over
                        shouldSendMessage = false;
                    }
                }
            } else {
                // The message is not for me, so I forward it
                for (Edge<LongWritable, FloatWritable> edge : vertex.getEdges()) {
                    // If the message has already passed through this vertex, this is where it comme from
                    if (message.path.contains(edge.getTargetVertexId()))
                        continue;
                    message.path.push(vertex.getId()); // Add the current vertex to the path
                    sendMessage(edge.getTargetVertexId(), new Message(message.id, message.remainingHops.get() - 1, message.isReply, message.path));
                }
            }
        }

        // If the vertex is its neighbour leader, then it participates in the election
        if (shouldSendMessage && vertex.getValue().get() == vertex.getId().get()) {
            // Send a message to each neighbor
            sendMessageToAllEdges(vertex, new Message(vertex.getId(), new LongArrayListWritable(vertex.getId()), Math.pow(2, getSuperstep()) - 1, false));
        }


        if (getSuperstep() != 0)
            vertex.voteToHalt();

    }

    public static class Message implements Writable {
        private DoubleWritable id;
        private LongArrayListWritable path; // Contains the path from the source to the vertex
        private DoubleWritable remainingHops;
        private BooleanWritable isReply;

        public Message() {
            set(new DoubleWritable(), new LongArrayListWritable(), new DoubleWritable(), new BooleanWritable());
        }

        public Message(DoubleWritable id, LongArrayListWritable path, double remainingHops, boolean isReply) {
            set(id, path, new DoubleWritable(remainingHops), new BooleanWritable(isReply));
        }
        public Message(LongWritable id, LongArrayListWritable path, double remainingHops, boolean isReply) {
            set(new DoubleWritable(id.get()), path, new DoubleWritable(remainingHops), new BooleanWritable(isReply));
        }

        public Message(DoubleWritable id, LongArrayListWritable path, DoubleWritable remainingHops, BooleanWritable isReply) {
            set(id, path, remainingHops, isReply);
        }

        public Message(DoubleWritable id, double v, BooleanWritable isReply, LongArrayListWritable path) {
            set(id, path, new DoubleWritable(v), isReply);
        }

        private void set(DoubleWritable id, LongArrayListWritable path, DoubleWritable remainingHops, BooleanWritable isReply) {
            this.id = id;
            this.path = path;
            this.remainingHops = remainingHops;
            this.isReply = isReply;
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
            this.id.write(dataOutput);
            this.path.write(dataOutput);
            this.remainingHops.write(dataOutput);
            this.isReply.write(dataOutput);
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
            this.id.readFields(dataInput);
            this.path.readFields(dataInput);
            this.remainingHops.readFields(dataInput);
            this.isReply.readFields(dataInput);
        }
    }

    public static class LongArrayListWritable
            extends ArrayListWritable<LongWritable> {
        /** Default constructor for reflection */
        public LongArrayListWritable() {
            super();
        }

        public LongArrayListWritable(LongWritable... ids) {
            this.addAll(Arrays.asList(ids));
        }

        public LongWritable pop() {
            return this.remove(this.size() - 1);
        }

        /** Set storage type for this ArrayListWritable */
        @Override
        @SuppressWarnings("unchecked")
        public void setClass() {
            setClass(LongWritable.class);
        }

        public void push(LongWritable id) {
            this.add(id);
        }
    }
}
