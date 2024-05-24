package fww.regular.buffer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class MultiBufferWriter implements Runnable {
    private final MultiBuffer buffer;
    private final String path;

    public MultiBufferWriter(String path, MultiBuffer buffer) {
        this.path = path;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try (Reader reader = new FileReader(path)) {
            int status;
            MultiBufferNode first = buffer.getFirst();
            first.setFull(true);
            status = reader.read(first.getBuffer());
            if (status == -1) {
                return;
            }
            while (true) {
                MultiBufferNode target = buffer.getLast().next();
                for (MultiBufferNode i = target; i != buffer.getFirst(); i = i.next()) {
                    if (!target.isFull()) {
                        Arrays.fill(target.getBuffer(), Tool.NIL);
                        status = reader.read(target.getBuffer());
                        target.setFull(true);
                        buffer.setLast(target);
                        if (status == -1) {
                            System.out.println("write end");
                            synchronized (buffer) {
                                buffer.notify();
                            }
                            return;
                        }
                    }
                }
                synchronized (buffer){
                    buffer.notify();
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
