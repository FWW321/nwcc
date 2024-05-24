package fww.regular.buffer;

public class MultiBufferNode {
    private final char[] buffer;
    private final int size;
    private boolean isFull = false;
    private MultiBufferNode next;
    private MultiBufferNode prev;

    public MultiBufferNode(int size){
        this.size = size;
        buffer = new char[size];
    }

    public void setFull(boolean isFull){
        this.isFull = isFull;
    }

    public boolean isFull(){
        return isFull;
    }

    public int getSize(){
        return size;
    }

    public char[] getBuffer(){
        return buffer;
    }

    public MultiBufferNode next(){
        return next;
    }

    public MultiBufferNode prev(){
        return prev;
    }

    public void setPrev(MultiBufferNode prev){
        this.prev = prev;
    }

    public void setNext(MultiBufferNode next){
        this.next = next;
    }

    public MultiBufferNode addNext(){
        MultiBufferNode next = new MultiBufferNode(size);
        next.next = this.next;
        this.next = next;
        next.prev = this;
        if(next.next != null){
            next.next.prev = next;
        }
        return next;
    }

    public MultiBufferNode addPrev(){
        MultiBufferNode prev = new MultiBufferNode(size);
        prev.prev = this.prev;
        this.prev = prev;
        prev.next = this;
        if(prev.prev != null){
            prev.prev.next = prev;
        }
        return prev;
    }

    public char get(int index){
        return buffer[index];
    }
}
