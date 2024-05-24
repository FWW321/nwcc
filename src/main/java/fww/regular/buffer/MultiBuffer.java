package fww.regular.buffer;

public class MultiBuffer {
    private MultiBufferNode first;
    private MultiBufferNode last;
    private MultiBufferNode current;
    private int beginIndex;
    private int currentIndex;
    private final int bufferSize;
    private int currentSize;
    private int maxSize;
    private final int minSize;
    private final MultiBufferNode head;

    public MultiBuffer(int minSize, int maxSize, int bufferSize){
        this.bufferSize = bufferSize;
        this.minSize = minSize;
        this.maxSize = maxSize;
        beginIndex = 0;
        currentIndex = bufferSize - 1;
        first = new MultiBufferNode(bufferSize);
        this.head = first;
        first.setFull(true);
        current = first;
        last = first;
        for(int i = 0; i < minSize; i++){
            if(i == 0){
                last = last.addNext();
            }else{
                MultiBufferNode t = last;
                last = last.addNext();
                last.setPrev(t);
            }
        }
        first = first.next();
        last.setNext(first);
        first.setPrev(last);
        last = first;
        this.currentSize = minSize;
    }

    public MultiBuffer(){
        this(1, 10, 128);
    }

    public void setMaxSize(int maxSize){
        this.maxSize = maxSize;
    }

    public int getMaxSize(){
        return maxSize;
    }

    public int getMinSize(){
        return minSize;
    }

    public boolean currentNext(){
        if(currentIndex >= bufferSize -1){
            MultiBufferNode next = current.next();
            if(next.isFull()){
                current = next;
                next.setFull(false);
                currentIndex = 0;
            }else {
                if(next == first && current != head){
                    if(currentSize < maxSize){
                        current.addNext();
                        currentSize++;
                    }else {
                        System.out.println("缓冲区不足, 请增加缓冲区大小");
                        throw new RuntimeException("缓冲区不足, 请增加缓冲区大小");
                    }
                }
                return false;
            }
        }else{
            currentIndex++;
        }
        return true;
    }

    public MultiBufferNode lastNext(){
        MultiBufferNode next = last.next();
        if(next == first){
            return null;
        }else {
            last = next;
            return next;
        }
    }

    //获取当前字符之后必须调用一次currentNext(), 除非获取到EOF
    public char getCurrent(){
        return current.get(currentIndex);
    }

    //commit之后必须获取一次当前字符
    public void commit(){
        beginIndex = currentIndex;
        first = current;
    }

    public int undo(int num){
        if(num <= 0){
            return 0;
        }
        int result = 0;
        for(int i = 0; i < num; i++){
            if(first == current){
                if(currentIndex != beginIndex){
                    currentIndex--;
                    result++;
                }
            }else{
                if(currentIndex != 0){
                    currentIndex--;
                    result++;
                }else{
                    current = current.prev();
                    currentIndex = bufferSize - 1;
                    result++;
                }
            }
        }
        return result;
    }

    public MultiBufferNode getFirst(){
        return this.first;
    }

    public MultiBufferNode getLast(){
        return this.last;
    }

    public void setLast(MultiBufferNode last){
        this.last = last;
    }
}
