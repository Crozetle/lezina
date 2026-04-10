
public class Motorcycle implements Transport, Cloneable {

    private static class Model {
        String name = null;
        double price = Double.NaN;
        Model prev = null;
        Model next = null;

        Model() {}

        Model(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private Model head = new Model();

    {
        head.prev = head;
        head.next = head;
    }

    private String brand;
    private int size = 0;

    public Motorcycle(String brand) {
        this.brand = brand;
        for (int i = 0; i < 1; i++) {
            insertLast(new Model());
            size++;
        }
    }

    private void insertLast(Model node) {
        node.prev = head.prev;
        node.next = head;
        head.prev.next = node;
        head.prev = node;
    }

    private Model findByName(String name) {
        for (Model cur = head.next; cur != head; cur = cur.next) {
            if (name.equals(cur.name)) return cur;
        }
        return null;
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
        String[] names = new String[size];
        int i = 0;
        for (Model cur = head.next; cur != head; cur = cur.next) {
            names[i++] = cur.name;
        }
        return names;
    }

    @Override
    public double[] getModelPrices() {
        double[] prices = new double[size];
        int i = 0;
        for (Model cur = head.next; cur != head; cur = cur.next) {
            prices[i++] = cur.price;
        }
        return prices;
    }

    @Override
    public double getModelPrice(String name) throws NoSuchModelNameException {
        Model m = findByName(name);
        if (m == null) throw new NoSuchModelNameException(name);
        return m.price;
    }

    @Override
    public void setModelPrice(String name, double price) throws NoSuchModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);
        Model m = findByName(name);
        if (m == null) throw new NoSuchModelNameException(name);
        m.price = price;
    }

    @Override
    public void setModelName(String oldName, String newName)
            throws NoSuchModelNameException, DuplicateModelNameException {
        if (findByName(newName) != null) throw new DuplicateModelNameException(newName);
        Model m = findByName(oldName);
        if (m == null) throw new NoSuchModelNameException(oldName);
        m.name = newName;
    }

    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);

        if (findByName(name) != null) throw new DuplicateModelNameException(name);

        for (Model cur = head.next; cur != head; cur = cur.next) {
            if (cur.name == null) {
                cur.name = name;
                cur.price = price;
                return;
            }
        }

        insertLast(new Model(name, price));
        size++;
    }

    @Override
    public void removeModel(String name) throws NoSuchModelNameException {
        Model m = findByName(name);
        if (m == null) throw new NoSuchModelNameException(name);

        m.prev.next = m.next;
        m.next.prev = m.prev;
        size--;
    }

    @Override
    public int getModelCount() {
        return size;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        Motorcycle clone = (Motorcycle) super.clone();

        clone.head = new Model();
        clone.head.prev = clone.head;
        clone.head.next = clone.head;
        clone.size = 0;

        for (Model cur = this.head.next; cur != this.head; cur = cur.next) {
            Model newNode = new Model();
            newNode.name = cur.name;
            newNode.price = cur.price;
            clone.insertLast(newNode);
            clone.size++;
        }

        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Мотоцикл [").append(brand).append("]\n");
        for (Model cur = head.next; cur != head; cur = cur.next) {
            sb.append("  Модель: ").append(cur.name)
                    .append(", цена: ").append(cur.price).append("\n");
        }
        return sb.toString();
    }
}