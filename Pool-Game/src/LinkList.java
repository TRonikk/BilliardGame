public class LinkList {
    // Начало списка
    private Node first;

    // Создание пустого списка
    public LinkList() {
        first = null;
    }

    // Добавление элемента в конец списка
    public void insert(Ball ball) {
        Node newNode = new Node(ball);
        newNode.next = first;
        first = newNode;
    }


    /*
     * Возвращает массив, содержащий все элементы списка
     * Если список пустой -возвращает массив нулевой длины
     */
    public Ball[] getElements() {
        int count = 0; // Для подсчета элементов в спсике
        Node runner; // Для обхода списка
        Ball[] elements;
        runner = first;
        while (runner != null) {
            count++;
            runner = runner.next;
        }
        elements = new Ball[count];
        runner = first;
        count--;
        while (runner != null) {
            elements[count] = runner.ball;
            count--;
            runner = runner.next;
        }
        return elements;
    }
}