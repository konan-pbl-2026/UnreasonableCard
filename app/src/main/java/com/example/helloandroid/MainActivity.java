
package com.example.helloandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int playerLife = 3;
    private int enemyLife = 3;

    private ArrayList<Integer> playerHand;
    private ArrayList<Integer> enemyHand;

    private TextView lifeText;
    private TextView handText;
    private TextView resultText;

    private Button guButton;
    private Button chokiButton;
    private Button paButton;

    private final String[] hands = {"グー", "チョキ", "パー"};
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lifeText = findViewById(R.id.lifeText);
        handText = findViewById(R.id.handText);
        resultText = findViewById(R.id.resultText);

        guButton = findViewById(R.id.guButton);
        chokiButton = findViewById(R.id.chokiButton);
        paButton = findViewById(R.id.paButton);

        guButton.setOnClickListener(v -> playJanken(0));
        chokiButton.setOnClickListener(v -> playJanken(1));
        paButton.setOnClickListener(v -> playJanken(2));

        startGame();
    }

    // 山札を作り、お互いに5枚配る
    private void startGame() {
        playerLife = 3;
        enemyLife = 3;

        ArrayList<Integer> deck = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            deck.add(0); // グー
            deck.add(1); // チョキ
            deck.add(2); // パー
        }

        Collections.shuffle(deck);

        playerHand = new ArrayList<>(deck.subList(0, 5));
        enemyHand = new ArrayList<>(deck.subList(5, 10));

        updateScreen();

        resultText.setText("手札からカードを選んでください");
        setButtonsEnabled(true);
    }

    // カードを1枚選んで勝負
    private void playJanken(int playerChoice) {
        if (playerLife <= 0 || enemyLife <= 0
                || playerHand.isEmpty() || enemyHand.isEmpty()) {
            return;
        }

        // 選んだカードを手札から1枚取り除く
        if (!playerHand.remove(Integer.valueOf(playerChoice))) {
            resultText.setText("そのカードは手札にありません！");
            return;
        }

        // 相手は残りの手札からランダムに1枚選ぶ
        int enemyIndex = random.nextInt(enemyHand.size());
        int enemyChoice = enemyHand.remove(enemyIndex);

        String message = "あなた：" + hands[playerChoice]
                + "\n相手：" + hands[enemyChoice] + "\n";

        if (playerChoice == enemyChoice) {
            message += "あいこ！ライフは変化なし";
        } else if ((playerChoice - enemyChoice + 3) % 3 == 2) {
            enemyLife--;
            message += "あなたの勝ち！相手のライフ -1";
        } else {
            playerLife--;
            message += "あなたの負け！自分のライフ -1";
        }

        updateScreen();

        // ゲーム終了判定
        if (playerLife <= 0 || enemyLife <= 0) {
            if (playerLife <= 0 && enemyLife <= 0) {
                message += "\n\n引き分け！";
            } else if (enemyLife <= 0) {
                message += "\n\nゲーム終了！あなたの勝ち！";
            } else {
                message += "\n\nゲーム終了！あなたの負け！";
            }
            endGame(message);
            return;
        }

        if (playerHand.isEmpty() || enemyHand.isEmpty()) {
            if (playerLife > enemyLife) {
                message += "\n\n手札がなくなりました！あなたの勝ち！";
            } else if (playerLife < enemyLife) {
                message += "\n\n手札がなくなりました！あなたの負け！";
            } else {
                message += "\n\n手札がなくなりました！引き分け！";
            }
            endGame(message);
            return;
        }

        resultText.setText(message);
    }

    // ライフと手札の表示を更新
    private void updateScreen() {
        lifeText.setText(
                "あなたのライフ：" + playerLife
                        + "　相手のライフ：" + enemyLife
        );

        handText.setText(
                "あなたの手札（" + playerHand.size() + "枚）："
                        + handToString(playerHand)
                        + "\n相手の手札：" + enemyHand.size() + "枚"
        );

        guButton.setEnabled(playerHand.contains(0));
        chokiButton.setEnabled(playerHand.contains(1));
        paButton.setEnabled(playerHand.contains(2));
    }

    private String handToString(ArrayList<Integer> hand) {
        StringBuilder text = new StringBuilder();

        for (int card : hand) {
            text.append(hands[card]).append(" ");
        }

        return text.toString();
    }

    private void endGame(String message) {
        resultText.setText(message);
        setButtonsEnabled(false);
    }

    private void setButtonsEnabled(boolean enabled) {
        guButton.setEnabled(enabled);
        chokiButton.setEnabled(enabled);
        paButton.setEnabled(enabled);
    }
}
