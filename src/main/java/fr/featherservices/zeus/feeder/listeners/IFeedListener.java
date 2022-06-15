package fr.featherservices.zeus.feeder.listeners;

public interface IFeedListener<T> {

    String getName();
    void listen(T t);
}
