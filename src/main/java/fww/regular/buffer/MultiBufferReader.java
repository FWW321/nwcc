package fww.regular.buffer;

public class MultiBufferReader implements Runnable {
    private final MultiBuffer buffer;

    public MultiBufferReader(MultiBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void run(){
        boolean flag;
        char c;
        while(true){
            flag = buffer.currentNext();
            if(!flag){
                doNotify();
                doWait();
                continue;
            }
            c = buffer.getCurrent();
            if(c == Tool.NIL){
                return;
            }
            System.out.println(c);
        }
    }

    public void doWait() {
        synchronized (buffer) {
            try {
                buffer.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void doNotify() {
        synchronized (buffer) {
            buffer.notify();
        }
    }

    public static void main(String[] args) {
        MultiBuffer buffer = new MultiBuffer();
        MultiBufferWriter writer = new MultiBufferWriter("src/main/java/fww/regular/buffer/MultiBufferReader.java", buffer);
        Thread writerThread = new Thread(writer);
//        MultiBufferReader reader = new MultiBufferReader(buffer);
//        Thread readerThread = new Thread(reader);
        writerThread.start();
//        readerThread.start();
    }
}
