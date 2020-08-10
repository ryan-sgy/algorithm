import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*  java 并发包中的锁（实现了Lock接口的ReentrantLock、ReentrantReadWriteLock）有一个newCondition方法。
调用该方法会返回与该锁绑定Condition对象实例。当线程获取锁之后，调用Condition实例的await方法会自动释放线程的锁，
当其他线程调用该Condition对象实例的signal方法后，该线程会自动尝试获取锁。
   通过对Condition的分析可知，我们只要对三个线程生成三个Condition对象。当一个线程打印一个数字之后就调用下一个线
   程的Condition对象的signal方法唤醒下一个线程，然后调用自己的Condition的await线程进入等待状态。这样就实现了线程执行顺序的控制。由于线程的执行是一个环形的队列，我们用一个数组存放每个线程的Condition对象，通过对下标加一然后取模来实现环形队列。
   */

public class PrintNumber extends Thread {
    /**
     * 多个线程共享这一个sequence数据
     */
    private static int sequence = 0;

    private static final int SEQUENCE_END = 75;

    private Integer id;
    private ReentrantLock lock;
    private Condition[] conditions;

    private PrintNumber(Integer id, ReentrantLock lock, Condition[] conditions) {
        this.id = id;
        this.setName("thread" + id);
        this.lock = lock;
        this.conditions = conditions;
    }

    @Override
    public void run() {
        while (sequence >= 0 && sequence < SEQUENCE_END) {
            lock.lock();
            try {
                // 对序号取模,如果不等于当前线程的id,则先唤醒其他线程,然后当前线程进入等待状态
                while (sequence % conditions.length != id) {
                    conditions[(id + 1) % conditions.length].signal();
                    conditions[id].await();
                }
                System.out.println(Thread.currentThread().getName() + " " + sequence);
                // 序号加1
                sequence = sequence + 1;
                // 唤醒当前线程的下一个线程
                conditions[(id + 1) % conditions.length].signal();
                // 当前线程进入等待状态
                conditions[id].await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 将释放锁的操作放到finally代码块中,保证锁一定会释放
                lock.unlock();
            }
        }
        // 数字打印完毕,线程结束前唤醒其余的线程,让其他线程也可以结束
        end();
    }

    private void end() {
        lock.lock();
        conditions[(id + 1) % conditions.length].signal();
        conditions[(id + 2) % conditions.length].signal();
        lock.unlock();
    }

    public static void main(String[] args) {
        int threadCount = 3;
        ReentrantLock lock = new ReentrantLock();
        Condition[] conditions = new Condition[threadCount];
        for (int i = 0; i < threadCount; i++) {
            conditions[i] = lock.newCondition();
        }
        PrintNumber[] printNumbers = new PrintNumber[threadCount];
        for (int i = 0; i < threadCount; i++) {
            PrintNumber p = new PrintNumber(i, lock, conditions);
            printNumbers[i] = p;
        }
        for (PrintNumber printNumber : printNumbers) {
            printNumber.start();
        }
    }

}