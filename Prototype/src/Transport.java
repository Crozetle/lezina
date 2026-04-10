
public interface Transport {
    void addModel(String name, double price) throws DuplicateModelNameException;

    void removeModel(String name) throws NoSuchModelNameException;


    String getBrand();

    double[] getModelPrices();

    String[] getModelNames();

    double getModelPrice(String name) throws NoSuchModelNameException;

    int getModelCount();


    void setBrand(String brand);

    void setModelPrice(String name, double price) throws NoSuchModelNameException;

    void setModelName(String oldName, String newName) throws NoSuchModelNameException, DuplicateModelNameException;

}
