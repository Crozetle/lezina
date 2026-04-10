import java.util.Arrays;

public class Car implements Transport, Cloneable {

    static class Model implements Cloneable {
        String name;
        double price;

        Model(String name, double price) {
            this.name = name;
            this.price = price;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private String brand;
    private Model[] models;

    public Car(String brand) {
        this.brand = brand;
        this.models = new Model[1];
    }

    @Override
    public String getBrand() {
        return brand;
    }

    @Override
    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String[] getModelNames() {
        String[] names = new String[models.length];
        for (int i = 0; i < models.length; i++) {
            names[i] = (models[i] != null) ? models[i].name : null;
        }
        return names;
    }

    @Override
    public double[] getModelPrices() {
        double[] prices = new double[models.length];
        for (int i = 0; i < models.length; i++) {
            prices[i] = (models[i] != null) ? models[i].price : Double.NaN;
        }
        return prices;
    }

    @Override
    public double getModelPrice(String name) throws NoSuchModelNameException {
        for (Model m : models) {
            if (m != null && m.name.equals(name))
                return m.price;
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public void setModelPrice(String name, double price) throws NoSuchModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);
        for (Model m : models) {
            if (m != null && m.name.equals(name)) {
                m.price = price;
                return;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public void setModelName(String oldName, String newName)
            throws NoSuchModelNameException, DuplicateModelNameException {

        for (Model m : models) {
            if (m != null && m.name.equals(newName))
                throw new DuplicateModelNameException(newName);
        }
        for (Model m : models) {
            if (m != null && m.name.equals(oldName)) {
                m.name = newName;
                return;
            }
        }
        throw new NoSuchModelNameException(oldName);
    }

    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);

        for (Model m : models) {
            if (m != null && m.name.equals(name))
                throw new DuplicateModelNameException(name);
        }

        for (int i = 0; i < models.length; i++) {
            if (models[i] == null) {
                models[i] = new Model(name, price);
                return;
            }
        }

        models = Arrays.copyOf(models, models.length + 1);
        models[models.length - 1] = new Model(name, price);
    }


    @Override
    public void removeModel(String name) throws NoSuchModelNameException {
        int idx = -1;
        for (int i = 0; i < models.length; i++) {
            if (models[i] != null && models[i].name.equals(name)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) throw new NoSuchModelNameException(name);

        Model[] newModels = Arrays.copyOf(models, models.length - 1);
        System.arraycopy(models, idx + 1, newModels, idx, models.length - idx - 1);

        models = newModels;
    }

    @Override
    public int getModelCount() {
        return models.length;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Car clone = (Car) super.clone();

        clone.models = new Model[this.models.length];

        for (int i = 0; i < this.models.length; i++) {
            if (this.models[i] != null) {
                clone.models[i] = (Model) this.models[i].clone();
            }
        }

        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Автомобиль [").append(brand).append("]\n");
        for (Model m : models) {
            if (m != null) {
                sb.append("  Модель: ").append(m.name)
                        .append(", цена: ").append(m.price).append("\n");
            }
        }
        return sb.toString();
    }
}