package fww.regular.buffer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

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
            status = reader.read(first.getBuffer());
            if (status == -1) {
                return;
            }
            while (true) {
                MultiBufferNode target = buffer.getLast().next();
                for (MultiBufferNode i = target; i != buffer.getFirst(); target = target.next()) {
                    if (!target.isFull()) {
                        status = reader.read(target.getBuffer());
                        target.setFull(true);
                        buffer.setLast(target);
                        if (status == -1) {
                            return;
                        }
                    }
                }
                doNotify();
                doWait();
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
