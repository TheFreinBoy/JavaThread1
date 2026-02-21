import java.util.Random;

 class ThreadDemo {

    static class Calculator implements Runnable {
        private final int threadId;
        private final int step;

        private volatile boolean canStop = false;

        public Calculator(int threadId, int step) {
            this.threadId = threadId;
            this.step = step;
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

            System.out.printf("[Потік %d] завершив роботу. Крок: %2d | Доданків: %9d | Сума: %d%n",
                    threadId, step, count, sum);
        }
    }

    public static void main(String[] args) {

        int threadCount = 4;
        Calculator[] tasks = new Calculator[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int threadId = i + 1;
            int step = threadId * 2;

            tasks[i] = new Calculator(threadId, step);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        Thread stopper = new Thread(() -> {
            Random rnd = new Random();

            for (int i = 0; i < threadCount; i++) {
                try {
                    int delay = 1000 + rnd.nextInt(1000);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Керуючий потік було перервано.");
                }

                tasks[i].stopThread();
            }
        });

        stopper.start();
    }
}