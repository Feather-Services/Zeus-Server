package fr.featherservices.zeus.feeder;

public interface IFeeder<T> {
    String getName();

    T feed();

    void publish();
}
