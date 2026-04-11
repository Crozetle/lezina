public interface Transport {
    void addModel(String name, double price);

    void removeModel(String name);

    void removeModelAlt(String name);

    String getBrand();

    double[] getModelPrices();

    String[] getModelNames();

    double getModelPrice(String name);

    int getModelCount();

    void setBrand(String brand);

    void setModelPrice(String name, double price);

    void setModelName(String oldName, String newName);
}
