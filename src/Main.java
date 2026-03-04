import java.util.Scanner;

class ThreadDemo {

    static class Calculator implements Runnable {
        private final int threadId;
        private final int step;
        private final int workTimeSeconds;

        private volatile boolean canStop = false;

        public Calculator(int threadId, int step, int workTimeSeconds) {
            this.threadId = threadId;
            this.step = step;
            this.workTimeSeconds = workTimeSeconds;
        }

        public void stopThread() {
            this.canStop = true;
        }

        @Override
        public void run() {
            long sum = 0;
            long count = 0;
            long currentNumber = 0;

            while (!canStop) {
                sum += currentNumber;
                count++;
                currentNumber += step;
            }

            System.out.printf("[Потік №%d] Сума: %d | Крок: %d | Доданків: %d | Час: %d сек.%n",
                    threadId, sum, step, count, workTimeSeconds);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введіть крок роботи потоків:");
        int step = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Введіть час роботи потоків:");
        String timesInput = scanner.nextLine().trim();

        String[] timesStr = timesInput.split("\\s+");
        int threadCount = timesStr.length;
        int[] workTimes = new int[threadCount];

        for (int i = 0; i < threadCount; i++) {
            workTimes[i] = Integer.parseInt(timesStr[i]);
        }

        Calculator[] tasks = new Calculator[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            tasks[i] = new Calculator(i + 1, step, workTimes[i]);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        Thread stopper = new Thread(() -> {
            long startTime = System.nanoTime();
            int activeCount = threadCount;
            boolean[] stopped = new boolean[threadCount];

            while (activeCount > 0) {
                double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;

                for (int i = 0; i < threadCount; i++) {
                    if (!stopped[i] && elapsedSeconds >= workTimes[i]) {
                        tasks[i].stopThread();
                        stopped[i] = true;
                        activeCount--;
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        stopper.start();

        try {
            for (Thread t : threads) {
                t.join();
            }
            stopper.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Всі потоки завершили роботу.");
        scanner.close();
    }
}