
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.Random;
import javax.swing.*;

class MyFrame extends JFrame implements ActionListener {

    private Container cp = getContentPane(); //視窗
    private URL[] iconURL = new URL[8];
    private JButton[] imgbtn = new JButton[16]; //圖片按鈕
    private ImageIcon ico[] = new ImageIcon[16]; //圖片存取
    private JButton start = new JButton("開始"); //開始按鈕
    private JButton timetostart = new JButton("時間");
    private JTextField right = new JTextField("0");//顯示分數
    private JTextField wrong = new JTextField("0");//顯示錯誤次數
    private JLabel rig = new JLabel("正確");
    private JLabel wro = new JLabel("錯誤");
    private int showsec = 10000; //設定可看幾秒(1000=1sec)
    private int locksec = 3000; //設定翻開後停留秒數 
    private int PosX = 0;//初始X座標
    private int PosY = 0;//初始Y座標
    private int index = -1;//第一次翻牌的標記
    private int poke = 0; //檢查翻牌數
    private boolean isStart = false; //是否開始
    private boolean[] btndown = new boolean[16]; //按鈕是否按下
    private boolean[] imgShow = new boolean[16]; //記錄已配對成功過的圖
    private boolean isstarted = false; //檢查遊戲是否開始
    private boolean tworound = false; //檢查是否第一次翻牌
    ClassLoader cl = this.getClass().getClassLoader();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    protected MyFrame(int x, int y) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cp.setLayout(null);
        setTitle("翻牌遊戲");
        start.setBounds(10, 720, 80, 40);
        timetostart.setBounds(100, 720, 120, 40);
        right.setBounds(640, 720, 50, 40);
        rig.setBounds(600, 720, 40, 40);
        wrong.setBounds(500, 720, 50, 40);
        wro.setBounds(460, 720, 40, 40);
        start.addActionListener(this);
        timetostart.addActionListener(this);
        cp.add(start);
        cp.add(timetostart);
        cp.add(right);
        cp.add(wrong);
        cp.add(rig);
        cp.add(wro);
        this.Create();
        setBounds((screen.width - x)/2, (screen.height - y)/2, x, y);
        setVisible(true);
        setResizable(false);
    }
    //建立圖片&位置
    private void Create() {

        for (int i = 0; i < ico.length; i += 2) {
            iconURL[i / 2] = cl.getResource("./imgs/" + (1 + (i / 2)) + ".png");
            ico[i] = new ImageIcon(iconURL[i / 2]);
            ico[i + 1] = new ImageIcon(iconURL[i / 2]);
        }
        
        for (int i = 0; i < imgbtn.length; i++) {
            if (i % 4 == 0 && i != 0) { PosX = 0; PosY += 180; }
            btndown[i] = false;
            imgbtn[i] = new JButton();
            imgbtn[i].setIcon(ico[i]);
            imgbtn[i].setBackground(null);
            imgbtn[i].setBounds(PosX, PosY, 180, 180);
            imgbtn[i].addActionListener(this);
            cp.add(imgbtn[i]);
            PosX += 180;
        }
    }

    //圖片洗牌
    private void Shuffle() {
        Point tmp = new Point(0, 0);
        int Ran;
        int index = 0;
        Random r = new Random();
        for (int i = 0; i < r.nextInt(33) + 33; i++) {
            Ran = r.nextInt(16);
            imgbtn[Ran].getLocation(tmp);
            imgbtn[Ran].setLocation(imgbtn[index].getX(), imgbtn[index].getY());
            imgbtn[index].setLocation(tmp);
            index = Ran;
        }
    }
    //按鈕功能表
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == start && !isstarted) {
            gmstart();
        }else if (e.getSource() == timetostart && isstarted && poke==0) {
            endgame();
        }
        for (int i = 0; i < imgbtn.length; i++) {
            if (e.getSource() == imgbtn[i] && isstarted && poke < 2) {
                check(i);
                break;
            }
        }
    }
    //遊戲啟動
    private void gmstart() {
        for (int i = 0; i < imgbtn.length; i++) {imgbtn[i].setIcon(ico[i]);}
        JOptionPane.showMessageDialog(null,
                "1.按下確認以後，玩家可以利用" + showsec / 1000  + "秒記圖片\n"
                + "2.翻錯圖片時，錯誤圖片會停留" + locksec / 1000 + "秒讓玩家記\n"
                + "3.猜對得1分，猜錯3次終止遊戲\n"
                + "4.分數顯示在右下角",
                "遊戲說明如下：",
                JOptionPane.INFORMATION_MESSAGE);
        this.start.setEnabled(false);
        this.Shuffle();
        new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        timetostart.setText("" + (10 - i));
                        Thread.sleep(showsec / 10 + 1);
                        timetostart.setText("重新開始");
                    }
                    isstarted = true;//確認開始遊戲
                    for (int i = 0; i < imgbtn.length; i++) {imgbtn[i].setIcon(null);}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //檢查正確錯誤
    private void check(int BtnNum) {
        if (!isstarted) return;
        if (btndown[BtnNum]) return;
        poke += 1;
        btndown[BtnNum] = true;
        imgbtn[BtnNum].setIcon(ico[BtnNum]);
        if (!tworound) { //判斷是否第一次翻牌
            index = BtnNum;
            tworound = true;
            return;
        }
        if (BtnNum / 2 == index / 2) { //正確
            imgShow[BtnNum] = true;
            imgShow[index] = true;
            tworound = false;
            Add();
            poke = 0;
            if (Integer.parseInt(right.getText()) == 8) {
                JOptionPane.showMessageDialog(null,
                        "恭喜你，全部答對了，錯誤次數:"+wrong.getText()+
                        "\n按下開始後，可以重新進行遊戲",
                        "成功囉!!",
                        JOptionPane.INFORMATION_MESSAGE);
                endgame();
            }
        } else { //失敗
            new Thread(new Runnable() {
                public void run() {
                    try { //錯誤 進行翻牌回去
                        Wrong();
                        if (Integer.parseInt(wrong.getText()) == 3) {
                            JOptionPane.showMessageDialog(null,
                                    "失敗，你得了"+right.getText()+"分，按下開始後，可重新進行遊戲",
                                    "失敗囉!!",
                                    JOptionPane.INFORMATION_MESSAGE);
                            endgame();
                            return;
                        }
                        tworound = false;
                        btndown[index] = false;
                        btndown[BtnNum] = false;
                        Thread.sleep(locksec);
                        imgbtn[BtnNum].setIcon(null);
                        imgbtn[index].setIcon(null);
                        poke = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    //分數
    private void Add() {
        this.right.setText("" + (Integer.parseInt(right.getText()) + 1));
    }
    //錯誤次數
    private void Wrong() {
        this.wrong.setText("" + (Integer.parseInt(wrong.getText()) + 1));
    }
    //結束遊戲重置
    private void endgame() {
        isstarted = false;
        tworound = false;
        index = -1;
        poke = 0;
        this.right.setText("0");
        this.wrong.setText("0");
        this.start.setEnabled(true);
        for (int i = 0; i < imgbtn.length; i++) {
            imgbtn[i].setIcon(null);
            imgShow[i] = false;
            btndown[i] = false;
        }
    }
}
public class PokeGame {
    public static void main(String[] args) {
        MyFrame mf = new MyFrame(730, 800);
    }
}
