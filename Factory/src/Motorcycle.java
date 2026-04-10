/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║           КЛАСС «МОТОЦИКЛ» (Motorcycle)                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║ Реализует те же операции, что и Car, но на основе              ║
 * ║ ДВУСВЯЗНОГО ЦИКЛИЧЕСКОГО СПИСКА С ГОЛОВОЙ.                     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Структура данных: двусвязный циклический список.
 *
 *  ┌──────┐  next  ┌──────┐  next  ┌──────┐  next
 *  │ head │ ──────► │  M1  │ ──────► │  M2  │ ──────► head
 *  │(dummy│ ◄────── │      │ ◄────── │      │ ◄──────
 *  └──────┘  prev  └──────┘  prev  └──────┘  prev
 *
 * head — «голова» (фиктивный узел). Данных не содержит.
 * Список «циклический»: последний элемент указывает на head,
 * а head.prev указывает на последний элемент.
 *
 * Преимущество: добавление/удаление O(1) без пересоздания массива.
 */
public class Motorcycle implements Transport {

    // ── Внутренний класс Model (узел списка) ─────────────────────────────
    private class Model {
        String name  = null;
        double price = Double.NaN;
        Model  prev  = null;   // ссылка на предыдущий узел
        Model  next  = null;   // ссылка на следующий узел
    }

    // ── Голова (head) — фиктивный узел ────────────────────────────────────
    //
    // Инициализатор экземпляра (блок в фигурных скобках без имени) выполняется
    // при каждом создании объекта, ДО конструктора.
    // Здесь он делает список пустым: head.prev = head.next = head.
    private Model head = new Model();
    {
        head.prev = head;
        head.next = head;
    }

    private String brand;
    private int    size = 0;  // текущее количество элементов

    // ── Конструктор ───────────────────────────────────────────────────────
    public Motorcycle(String brand) {
        this.brand = brand;
        // Создаём initialSize пустых узлов (как в задании, аналог new Model[size])
        for (int i = 0; i < 1; i++) {
            insertLast(new Model());
            size++;
        }
    }

    // ── Вспомогательный метод: вставить узел перед head (в конец списка) ──
    private void insertLast(Model node) {
        // Вставка перед головой = вставка в конец циклического списка
        node.prev       = head.prev;  // новый узел смотрит назад на старый последний
        node.next       = head;       // новый узел смотрит вперёд на голову
        head.prev.next  = node;       // старый последний смотрит вперёд на новый
        head.prev       = node;       // голова смотрит назад на новый узел
    }

    // ── Вспомогательный метод: найти узел по имени ────────────────────────
    private Model findByName(String name) {
        // Обходим список от head.next до head (не включительно)
        for (Model cur = head.next; cur != head; cur = cur.next) {
            if (name.equals(cur.name)) return cur;
        }
        return null;
    }

    // ── Геттер/Сеттер марки ───────────────────────────────────────────────
    @Override
    public String getBrand() { return brand; }

    @Override
    public void setBrand(String brand) { this.brand = brand; }

    // ── Массив имён моделей ───────────────────────────────────────────────
    @Override
    public String[] getModelNames() {
        String[] names = new String[size];
        int i = 0;
        for (Model cur = head.next; cur != head; cur = cur.next) {
            names[i++] = cur.name;
        }
        return names;
    }

    // ── Массив цен моделей ────────────────────────────────────────────────
    @Override
    public double[] getModelPrices() {
        double[] prices = new double[size];
        int i = 0;
        for (Model cur = head.next; cur != head; cur = cur.next) {
            prices[i++] = cur.price;
        }
        return prices;
    }

    // ── Цена по имени ─────────────────────────────────────────────────────
    @Override
    public double getModelPrice(String name) throws NoSuchModelNameException {
        Model m = findByName(name);
        if (m == null) throw new NoSuchModelNameException(name);
        return m.price;
    }

    // ── Установить цену ───────────────────────────────────────────────────
    @Override
    public void setModelPrice(String name, double price) throws NoSuchModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);
        Model m = findByName(name);
        if (m == null) throw new NoSuchModelNameException(name);
        m.price = price;
    }

    // ── Переименовать модель ──────────────────────────────────────────────
    @Override
    public void setModelName(String oldName, String newName)
            throws NoSuchModelNameException, DuplicateModelNameException {
        if (findByName(newName) != null) throw new DuplicateModelNameException(newName);
        Model m = findByName(oldName);
        if (m == null) throw new NoSuchModelNameException(oldName);
        m.name = newName;
    }

    // ── Добавить модель ───────────────────────────────────────────────────
    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);

        // Проверяем дублирование имён
        if (findByName(name) != null) throw new DuplicateModelNameException(name);

        // Ищем первый незаполненный узел (name == null)
        for (Model cur = head.next; cur != head; cur = cur.next) {
            if (cur.name == null) {
                cur.name  = name;
                cur.price = price;
                return;
            }
        }

        // Нет пустого узла — добавляем новый в конец
        Model node = new Model();
        node.name  = name;
        node.price = price;
        insertLast(node);
        size++;
    }

    // ── Удалить модель ────────────────────────────────────────────────────
    @Override
    public void removeModel(String name) throws NoSuchModelNameException {
        Model m = findByName(name);
        if (m == null) throw new NoSuchModelNameException(name);

        // Удаление узла из двусвязного списка — просто перешиваем ссылки
        m.prev.next = m.next;  // предыдущий узел теперь смотрит на следующий
        m.next.prev = m.prev;  // следующий узел теперь смотрит на предыдущий
        size--;
    }

    // ── Количество моделей ────────────────────────────────────────────────
    @Override
    public int getModelCount() { return size; }

    // ── toString ──────────────────────────────────────────────────────────
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
