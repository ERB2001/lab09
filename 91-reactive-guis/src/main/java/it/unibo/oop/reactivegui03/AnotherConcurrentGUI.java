package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int START = 0;
    private static final int TIME = 100;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JPanel canvas = new JPanel();

        canvas.add(display);

        canvas.add(up);

        canvas.add(down);

        canvas.add(stop);

        this.getContentPane().add(canvas);

        this.setVisible(true);

        // timeStopAgent time = new timeStopAgent();

        Agent agent = new Agent();

        new Thread(agent).start();

        // new Thread(time).start();

        // time.decrementTime();

        CountDownTimer timer = new CountDownTimer();

        new Thread(timer).start();

        stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                agent.stopCounting();
                stop.setEnabled(true);
                up.setEnabled(true);
                down.setEnabled(true);
            }
        });

        /*
         * int time = 1000;
         * 
         * while (time != 0) {
         * try {
         * time--;
         * Thread.sleep(10);
         * System.out.println(time);
         * } catch (InterruptedException e) {
         * // TODO Auto-generated catch block
         * e.printStackTrace();
         * }
         * }
         * 
         * //così funziona --> trovare modo per farlo funzionare tramite nuovo Agent
         * 
         * if (time == 0) {
         * agent.stopCounting();
         * stop.setEnabled(true);
         * up.setEnabled(true);
         * down.setEnabled(true);
         * }
         */

        up.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                agent.upIncrementCounting();
            }
        });

        down.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                agent.downIncrementCounting();
            }
        });

        if (timer.getCondition() == START) {
            agent.stopCounting();
            stop.setEnabled(true);
            up.setEnabled(true);
            down.setEnabled(true);
        }

    }

    /*
     * private class timeStopAgent implements Runnable {
     * 
     * private Long time;
     * 
     * private volatile boolean stop;
     * 
     * public void run() {
     * 
     * time = 10000L;
     * 
     * }
     * 
     * public Long decrementTime() {
     * while (time != 0) {
     * try {
     * Thread.sleep(1);
     * time--;
     * } catch (InterruptedException e) {
     * // TODO Auto-generated catch block
     * e.printStackTrace();
     * }
     * System.out.println(time);
     * }
     * return time;
     * }
     * 
     * }
     */

    private class CountDownTimer implements Runnable {

        private int time = TIME;

        private boolean condition;

        // private boolean tmp;

        public void run() {

        }

        public int getCondition() {

            while (!condition) {
                try {
                    Thread.sleep(TIME);
                    time--;
                    if (time == START) {
                        condition = true;
                    }
                    System.out.println(time);
                    System.out.println(condition);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return time;

        }

        /*
         * if (time > 0) {
         * try {
         * Thread.sleep(10000);
         * time--;
         * } catch (InterruptedException e) {
         * // TODO Auto-generated catch block
         * e.printStackTrace();
         * }
         * } else {
         * return time;
         * }
         * return 1;
         * }
         */

    }

    private class Agent implements Runnable {

        private volatile boolean stop;

        private volatile boolean change;

        private int counter;

        // private volatile boolean decision;

        public void run() {

            while (!this.stop) {

                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile
                    final var nextText = Integer.toString(this.counter);

                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));

                    if (!change) {

                        this.counter++;

                        // Thread.sleep(100);

                    } else if (change) {

                        this.counter--;

                        // Thread.sleep(100);

                    }

                    Thread.sleep(TIME);

                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void upIncrementCounting() {
            this.change = false;
        }

        public void downIncrementCounting() {
            this.change = true;
        }

    }

}
