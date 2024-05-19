package fww.regular.buffer;

import java.io.FileReader;
import java.io.IOException;

public class DoubleBuffer {
    private final char[] buffer1;
    private final char[] buffer2;
    private final int size;
    private int beginIndex;
    private int forwardIndex;
    private boolean isBuffer1Active;

    public DoubleBuffer(int size) {
        this.size = size;
        this.buffer1 = new char[size + 1]; // 为哨兵添加额外的空间
        this.buffer2 = new char[size + 1]; // 为哨兵添加额外的空间
        this.beginIndex = 0;
        this.forwardIndex = 0;
        this.isBuffer1Active = true;
    }

    public void readFromFile(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            char[] buffer;
            while (true) {
                buffer = isBuffer1Active ? buffer1 : buffer2;
                int readChars = reader.read(buffer, forwardIndex, size - forwardIndex);
                if (readChars == -1) {
                    break; // 文件结束
                }
                forwardIndex += readChars;
                if (forwardIndex == size) {
                    switchBuffer();
                    forwardIndex = 0; // 缓冲区切换后重置前进索引
                }
            }
        }
        // 在文件读取完成后，调用commit以保证数据的一致性
        commit();
    }

    public void commit() {
        beginIndex = forwardIndex;
    }

    public void switchBuffer() {
        isBuffer1Active = !isBuffer1Active; // 直接切换缓冲区
    }

    public char getCurrentCharacter() {
        char[] currentBuffer = isBuffer1Active ? buffer1 : buffer2;
        int currentIndex = forwardIndex % size; // 根据当前缓冲区和前进索引计算正确的索引
        forwardIndex++; // 前进一步
        return currentBuffer[currentIndex];
    }

    private void updateBufferFromInput(FileReader reader, char[] currentBuffer, int startIndex, int endIndex) throws IOException {
        // 初始化哨兵字符
        char sentinel = '\uffff'; // 设置一个文件中不会出现的字符

        int readChars;
        while ((readChars = reader.read(currentBuffer, startIndex, size - startIndex)) != -1) {
            forwardIndex += readChars;

            // 检查是否已到达文件末尾
            if (readChars < size - startIndex) {
                // 如果到达文件末尾，则插入哨兵字符
                currentBuffer[forwardIndex % size] = sentinel;
                break;
            }

            // 如果缓冲区已满，则退出
            if (forwardIndex % size == endIndex) {
                break;
            }

            // 如果需要，切换到另一个缓冲区
            if (forwardIndex % size == 0) {
                switchBuffer();
                currentBuffer = isBuffer1Active ? buffer1 : buffer2;
            }

            // 更新下一次迭代的索引
            startIndex = forwardIndex % size;
            endIndex = (forwardIndex + size - 1) % size;
        }
    }

    public void readAndCommit(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            char[] currentBuffer = isBuffer1Active ? buffer1 : buffer2;
            int startIndex = beginIndex % size;
            int endIndex = forwardIndex % size;
            updateBufferFromInput(reader, currentBuffer, startIndex, endIndex);
        }
        // 在文件读取完成后，调用commit以保证数据的一致性
        commit();
    }

    public static void main(String[] args) {
        DoubleBuffer doubleBuffer = new DoubleBuffer(128);
        try {
            doubleBuffer.readFromFile("src/main/java/fww/regular/buffer/DoubleBuffer.java");
            char c;
            while ((c = doubleBuffer.getCurrentCharacter()) != '\uffff') {
                System.out.print(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
