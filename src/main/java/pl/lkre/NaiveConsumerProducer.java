package pl.lkre;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class NaiveConsumerProducer {
    private static final Random generator = new Random();
    private static final Queue<String> queue = new LinkedList<>();

    public static void main(String[] args) {
        int itemCount = 5;
        Thread producer = new Thread(() -> {
            for (int i = 0; i < itemCount; i++) {
                try {
                    Thread.sleep(Duration.ofSeconds(generator.nextInt(5)).toMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (queue) {
                    queue.add("Item no. " + i);
                    queue.notify();
                }
            }
        });
        Thread consumer = new Thread(() -> {
            int itemsLeft = itemCount;
            while (itemsLeft > 0) {
                String item;
                synchronized (queue) {
                    int counter = 0;
                    //specyfikacja języka Java pozwala na fałszywe wybudzenia (spurious wake-ups), są to wybudzenia, które mogę wystąpić nawet jeśli nie było odpowiadającego im powiadomienia
                    // (wywołania metody notify). Dlatego sprawdzenie warunku queue.isEmpty() musi być wykonane w pętli.
                    while (queue.isEmpty()) {
                        counter++;
                        System.out.println("counter: " + counter);
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    item = queue.poll();
                    // worst sollution of while loop:
//                    if (queue.isEmpty()) {
//                        continue;
//                    }
//                    item = queue.poll();
                }

                itemsLeft--;
                System.out.println("Consumer got item: " + item);
            }
        });

        consumer.start();
        producer.start();
    }
}