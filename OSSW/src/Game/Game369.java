package Game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
public class Game369 extends JFrame{
    public Game369(){
        this.setTitle("369게임 둘이하기");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new GamePanel());
        this.setLocationRelativeTo(null);
        this.setSize(320, 300);
        this.setVisible(true);
    }
    
    class GamePanel extends JPanel{
        TimerThread th;//카운트 스레드 선언
        JLabel[] card = new JLabel[2];//보여줄 화면 2개 
        JButton start = new JButton("Start");//시작 버튼
        User[] user = new User[2];//2명의 플레이어

        int n=1;//카운트 변수
        char key;//입력받는 키
        boolean isKeyPressed = false;//키를 눌렀는지 체크

        GamePanel(){
            user[0]=new User("박지성, 키:AS",'a','s');
            user[1]=new User("이청룡, 키:KL",'k','l');

            this.setLayout(null);
            this.addKeyListener(new KeyHandler());
            
            //유저이름을 보여줄 레이블 생성과 패널에 추가
            for(int i=0; i<card.length; i++){
                JLabel name = new JLabel(user[i].getName());
                name.setFont(new Font("고딕",Font.ITALIC,15));
                name.setHorizontalAlignment(JLabel.CENTER);
                name.setSize(100, 50);
                name.setLocation(50+110*i, 50);
                this.add(name);
            }
            
            //보여줄 화면 레이블을 패널에 추가
            for(int i=0; i<card.length; i++){
                card[i]= new JLabel();
                card[i].setOpaque(true);
                card[i].setBackground(Color.orange);
                card[i].setFont(new Font("고딕",Font.ITALIC,30));
                card[i].setHorizontalAlignment(JLabel.CENTER);
                card[i].setSize(100,50);
                card[i].setLocation(50+110*i, 100);
                this.add(card[i]);
            }
            
            //시작 버튼
            start.setLocation(100,200);
            start.setSize(100, 30);
            start.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent ae) {//시작버튼이 눌렸을 때
                    //초기설정들
                    n=1;
                    card[0].setText("");
                    card[1].setText("");
                    
                    isKeyPressed=false;
                    card[(n+1)%2].setText(Integer.toString(n));
                    card[(n+1)%2].requestFocus();//차례차례돌아가며 입력하기위해 포커스

                    th = new TimerThread();
                    th.start();//스레드 시작

                    JButton b = (JButton)ae.getSource();
                    b.setEnabled(false);//버튼 비활성화

                    b.getParent().requestFocus();//컨텐트팬이 입력을 받을수있도록 포커스 전환
                }
            });
            this.add(start);
        }
        
        class TimerThread extends Thread{
            public void run(){
                while(true){
                    try{
                        sleep(700);//0.7초마다 숫자 카운트

                        int d1=n%10;//일의 자리
                        int d2=n/10;//십의 자리

                        int turn=(n+1)%2;//차례
                        int next=(turn+1)%2;//다음차례

                        if((d1==3||d1==6||d1==9)&&(d2==3||d2==6||d2==9)){//더블클릭 할 상황
                            if(isKeyPressed==true&&user[turn].isDoubleKey(key)){//성공시
                                isKeyPressed=false;
                            }
                            else if(isKeyPressed==true&&user[(turn+1)%2].isKey(key)){//다음차례 플레이어가 키를 눌렀을때
                                msg(next, "Fail");
                                break;
                            }
                            else{//못 눌렀을 때
                                msg(turn, "Fail");
                                break;
                            }
                        }
                        
                        else if(d1==3||d1==6||d1==9||d2==3||d2==6||d2==9){//한번 클릭해야하는 상황
                            if(isKeyPressed==true&&user[turn].isSingleKey(key)){//성공시
                                isKeyPressed=false;
                            }
                            else if(isKeyPressed==true&&user[next].isKey(key)){//다음차례 플레이어가 키를 눌렀을 시
                                msg(next, "Fail");
                                break;
                            }
                            else{//못 눌렀을 때
                                msg(turn, "Fail");
                                break;
                            }
                        }
                        
                        else{//누르지 말아야 할상황
                            if(isKeyPressed==true&&user[turn].isKey(key)){//해당 플레이어가 눌렀을 때
                                msg(turn, "Fail");
                                break;
                            }
                            else if(isKeyPressed==true&&user[next].isKey(key)){//다음 차례 플레이어 눌렀을 때
                                msg(next, "Fail");
                                break;
                            }
                            isKeyPressed=false;//초기설정으로 되돌림
                        }
                        n++;
                        if(n==100){//100까지 갔을 때
                            msg(0, "WIN!!");
                            msg(1, "WIN!!");
                            break;
                        }
                        else{//계속 게임 실행
                            card[next].setText(Integer.toString(n));
                        }
                    }
                    catch(Exception e){ return; }
                }
                start.setEnabled(true);//게임이 끝났을 때 시작버튼 활성화
            }
            
            //누가 졌는지 혹은 누가 이겼는지 출력해주기 위한 메서드
            void msg(int id, String s){
                card[id].setText(s);
            }
        }

        class KeyHandler implements KeyListener{

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                key=ke.getKeyChar();//입력된 키
                isKeyPressed =true;//눌림상태로 전환
                int turn=(n+1)%2;
                card[turn].setBackground(Color.green);//해당 턴에 누를시 초록색 배경
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                int turn = (n+1)%2;
                card[turn].setBackground(Color.orange);//다시 오렌지색 배경으로 전환
            } 
        }

        class User{
            String name;
            char singleKey;//한번 누름
            char doubleKey;//두번 누름
            
            User(String name, char singleKey, char doubleKey){
                this.name=name;
                this.singleKey=singleKey;
                this.doubleKey=doubleKey;
            }
            
            String getName(){
                return name;
            }
            
            boolean isKey(char key){
                return singleKey==key||doubleKey==key;
            }
            
            boolean isSingleKey(char key){
                return singleKey==key;
            }
            
            boolean isDoubleKey(char key){
                return doubleKey==key;
            }
        }
    }
   
}