class SynchronizedTransport implements Transport {

    private final Transport delegate;

    SynchronizedTransport(Transport delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized void addModel(String name, double price) throws DuplicateModelNameException {
        delegate.addModel(name, price);
    }

    @Override
    public synchronized void removeModel(String name) throws NoSuchModelNameException {
        delegate.removeModel(name);
    }

    @Override
    public synchronized void removeModelAlt(String name) throws NoSuchModelNameException {
        delegate.removeModelAlt(name);
    }

    @Override
    public synchronized String getBrand() {
        return delegate.getBrand();
    }

    @Override
    public synchronized double[] getModelPrices() {
        return delegate.getModelPrices();
    }

    @Override
    public synchronized String[] getModelNames() {
        return delegate.getModelNames();
    }

    @Override
    public synchronized double getModelPrice(String name) throws NoSuchModelNameException {
        return delegate.getModelPrice(name);
    }

    @Override
    public synchronized int getModelCount() {
        return delegate.getModelCount();
    }

    @Override
    public synchronized void setBrand(String brand) {
        delegate.setBrand(brand);
    }

    @Override
    public synchronized void setModelPrice(String name, double price) throws NoSuchModelNameException {
        delegate.setModelPrice(name, price);
    }

    @Override
    public synchronized void setModelName(String oldName, String newName)
            throws NoSuchModelNameException, DuplicateModelNameException {
        delegate.setModelName(oldName, newName);
    }
}
