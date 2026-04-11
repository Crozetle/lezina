// Декоратор (Decorator/Wrapper) — реализует тот же интерфейс Transport,
// что и оборачиваемый объект, и добавляет к каждому методу синхронизацию.
//
// Ключевые принципы паттерна Decorator в этом классе:
//   1. SynchronizedTransport содержит ссылку на «настоящий» Transport (delegate).
//   2. Каждый метод делегирует вызов этому объекту.
//   3. До делегирования захватывает монитор (synchronized) — добавленное поведение.
//
// Преимущество перед наследованием: не нужно расширять Car или Motorcycle.
// Декоратор добавляет потокобезопасность снаружи, не трогая реализацию классов.
// Это позволяет оборачивать любой объект, реализующий Transport, включая
// другие декораторы (цепочка декораторов).
class SynchronizedTransport implements Transport {

    // delegate — объект, выполняющий реальную работу.
    // final: ссылка не изменится после конструктора, что само по себе потокобезопасно
    // (final-поля безопасно публикуются между потоками по модели памяти Java).
    private final Transport delegate;

    SynchronizedTransport(Transport delegate) {
        this.delegate = delegate;
    }

    // Каждый метод синхронизирован по монитору this.
    // В любой момент только один поток может исполнять хотя бы один из них —
    // остальные встают в очередь. Это предотвращает гонки данных (data races)
    // при параллельных вызовах addModel, removeModel и setModelPrice.

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

    // Снимок массива: getModelPrices возвращает новый массив каждый раз,
    // поэтому синхронизация гарантирует, что снимок будет консистентным —
    // никакой другой поток не изменит список моделей в процессе копирования.
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
