package fww.regular.Interface;

@FunctionalInterface
public interface FailedFunction {
    void failed(String s, int line);
}
