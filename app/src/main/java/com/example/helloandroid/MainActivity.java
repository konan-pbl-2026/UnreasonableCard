package com.example.helloandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int playerLife = 3;
    private int enemyLife = 3;
    private boolean isSpecialMode = false; // 特殊モードフラグ

    private ArrayList<Integer> playerHand;
    private ArrayList<Integer> enemyHand;

    private TextView playerLifeText;
    private TextView enemyLifeText;
    private TextView resultText;

    private TextView battlePlayerCard;
    private TextView battleEnemyCard;

    private TextView finalPlayerLifeText;
    private TextView finalEnemyLifeText;
    private TextView finalResultText;

    private Button pauseButton;
    private LinearLayout cardContainer;
    private LinearLayout enemyCardContainer;

    private View startScreen;
    private LinearLayout helpScreen; // 説明画面
    private LinearLayout gameScreen;
    private LinearLayout pauseScreen;
    private LinearLayout resultScreen;

    private final String[] hands = {"✊", "✌️", "✋"};
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startScreen = findViewById(R.id.startScreen);
        helpScreen = findViewById(R.id.helpScreen);
        gameScreen = findViewById(R.id.gameScreen);
        pauseScreen = findViewById(R.id.pauseScreen);
        resultScreen = findViewById(R.id.resultScreen);

        Button normalModeButton = findViewById(R.id.normalModeButton);
        Button specialModeButton = findViewById(R.id.specialModeButton);
        Button helpButton = findViewById(R.id.helpButton);
        Button backToTitleButton = findViewById(R.id.backToTitleButton);

        playerLifeText = findViewById(R.id.playerLifeText);
        enemyLifeText = findViewById(R.id.enemyLifeText);
        resultText = findViewById(R.id.resultText);

        battlePlayerCard = findViewById(R.id.battlePlayerCard);
        battleEnemyCard = findViewById(R.id.battleEnemyCard);

        finalPlayerLifeText = findViewById(R.id.finalPlayerLifeText);
        finalEnemyLifeText = findViewById(R.id.finalEnemyLifeText);
        finalResultText = findViewById(R.id.finalResultText);

        cardContainer = findViewById(R.id.cardContainer);
        enemyCardContainer = findViewById(R.id.enemyCardContainer);

        pauseButton = findViewById(R.id.pauseButton);
        Button resumeButton = findViewById(R.id.resumeButton);
        Button pauseRetryButton = findViewById(R.id.pauseRetryButton);
        Button pauseTitleButton = findViewById(R.id.pauseTitleButton);

        Button retryButton = findViewById(R.id.retryButton);
        Button titleButton = findViewById(R.id.titleButton);

        // 通常モード
        normalModeButton.setOnClickListener(v -> {
            isSpecialMode = false;
            startScreen.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            startGame();
        });

        // 特殊モード（ライフ5・手札7枚）
        specialModeButton.setOnClickListener(v -> {
            isSpecialMode = true;
            startScreen.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            startGame();
        });

        // 遊び方説明ボタン（タイトル画面のみ）
        helpButton.setOnClickListener(v -> {
            startScreen.setVisibility(View.GONE);
            helpScreen.setVisibility(View.VISIBLE);
        });

        // 説明画面からタイトルに戻るボタン
        backToTitleButton.setOnClickListener(v -> {
            helpScreen.setVisibility(View.GONE);
            startScreen.setVisibility(View.VISIBLE);
        });

        // ポーズボタン
        pauseButton.setOnClickListener(v -> {
            gameScreen.setVisibility(View.GONE);
            pauseScreen.setVisibility(View.VISIBLE);
        });

        // ゲームに戻る
        resumeButton.setOnClickListener(v -> {
            pauseScreen.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
        });

        // ポーズ中に最初から
        pauseRetryButton.setOnClickListener(v -> {
            pauseScreen.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            startGame();
        });

        // ポーズ中にタイトルに戻る
        pauseTitleButton.setOnClickListener(v -> {
            pauseScreen.setVisibility(View.GONE);
            startScreen.setVisibility(View.VISIBLE);
        });

        // 対戦終了後にもう一度
        retryButton.setOnClickListener(v -> {
            resultScreen.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            startGame();
        });

        // 対戦終了後にタイトルに戻る
        titleButton.setOnClickListener(v -> {
            resultScreen.setVisibility(View.GONE);
            startScreen.setVisibility(View.VISIBLE);
        });
    }

    // ゲーム開始
    private void startGame() {
        if (isSpecialMode) {
            playerLife = 5;
            enemyLife = 5;
        } else {
            playerLife = 3;
            enemyLife = 3;
        }

        int cardCountPerHand = isSpecialMode ? 7 : 5;
        ArrayList<Integer> deck = new ArrayList<>();

        for (int i = 0; i < cardCountPerHand; i++) {
            deck.add(0); // ✊
            deck.add(1); // ✌️
            deck.add(2); // ✋
        }

        Collections.shuffle(deck);

        playerHand = new ArrayList<>(deck.subList(0, cardCountPerHand));
        enemyHand = new ArrayList<>(deck.subList(cardCountPerHand, cardCountPerHand * 2));

        pauseButton.setEnabled(true);
        battlePlayerCard.setVisibility(View.INVISIBLE);
        battleEnemyCard.setVisibility(View.INVISIBLE);

        updateScreen();

        if (isSpecialMode) {
            resultText.setText("【特殊モード】手札からカードを選んでください");
        } else {
            resultText.setText("手札からカードを選んでください");
        }
    }

    // カードをタップした時の処理
    private void playJanken(int playerChoice) {
        if (playerLife <= 0 || enemyLife <= 0
                || playerHand.isEmpty() || enemyHand.isEmpty()) {
            return;
        }

        pauseButton.setEnabled(false);
        setCardsEnabled(false);

        int enemyIndex = random.nextInt(enemyHand.size());
        int enemyChoice = enemyHand.get(enemyIndex);

        battlePlayerCard.setText(hands[playerChoice]);
        battlePlayerCard.setBackgroundResource(R.drawable.card_background);
        battlePlayerCard.setVisibility(View.VISIBLE);

        battleEnemyCard.setText("");
        battleEnemyCard.setBackgroundResource(R.drawable.enemy_card_background);
        battleEnemyCard.setVisibility(View.VISIBLE);

        resultText.setText("カードをセット！ 相手が選んでいます...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            battleEnemyCard.setText(hands[enemyChoice]);
            battleEnemyCard.setBackgroundResource(R.drawable.card_background);

            resolveJanken(playerChoice, enemyChoice, enemyIndex);

        }, 1000);
    }

    // じゃんけんの勝敗判定
    private void resolveJanken(int playerChoice, int enemyChoice, int enemyIndex) {
        playerHand.remove(Integer.valueOf(playerChoice));
        enemyHand.remove(enemyIndex);

        String message = "あなた：" + hands[playerChoice]
                + " / 相手：" + hands[enemyChoice] + "\n";

        if (playerChoice == enemyChoice) {
            message += "あいこ！ライフは変化なし";
        } else if ((playerChoice - enemyChoice + 3) % 3 == 2) {
            enemyLife--;
            message += "あなたの勝ち！相手のライフ -1";
        } else {
            playerLife--;
            message += "あなたの負け！自分のライフ -1";
        }

        resultText.setText(message);
        updateScreen();
        pauseButton.setEnabled(true);

        if (playerLife <= 0 || enemyLife <= 0) {
            if (playerLife <= 0 && enemyLife <= 0) {
                message += "\n引き分け！";
            } else if (enemyLife <= 0) {
                message += "\nゲーム終了！あなたの勝ち！";
            } else {
                message += "\nゲーム終了！あなたの負け！";
            }
            resultText.setText(message);
            endGame();
            return;
        }

        if (playerHand.isEmpty() || enemyHand.isEmpty()) {
            if (playerLife > enemyLife) {
                message += "\n手札がなくなりました！あなたの勝ち！";
            } else if (playerLife < enemyLife) {
                message += "\n手札がなくなりました！あなたの負け！";
            } else {
                message += "\n手札がなくなりました！引き分け！";
            }
            resultText.setText(message);
            endGame();
            return;
        }
    }

    // 画面表示の更新
    private void updateScreen() {
        enemyLifeText.setText("相手ライフ：" + getHearts(enemyLife));
        playerLifeText.setText("あなたライフ：" + getHearts(playerLife));

        cardContainer.removeAllViews();
        for (int i = 0; i < playerHand.size(); i++) {
            int cardType = playerHand.get(i);

            Button cardButton = new Button(this);
            cardButton.setText(hands[cardType]);
            cardButton.setTextSize(18);
            cardButton.setBackgroundResource(R.drawable.card_background);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 180);
            params.setMargins(8, 0, 8, 0);
            cardButton.setLayoutParams(params);

            cardButton.setOnClickListener(v -> playJanken(cardType));
            cardContainer.addView(cardButton);
        }

        enemyCardContainer.removeAllViews();
        for (int i = 0; i < enemyHand.size(); i++) {
            View enemyCardView = new View(this);
            enemyCardView.setBackgroundResource(R.drawable.enemy_card_background);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 180);
            params.setMargins(8, 0, 8, 0);
            enemyCardView.setLayoutParams(params);

            enemyCardContainer.addView(enemyCardView);
        }
    }

    private String getHearts(int life) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < life; i++) {
            sb.append("♥");
        }
        if (life <= 0) {
            sb.append("（なし）");
        }
        return sb.toString();
    }

    private void setCardsEnabled(boolean enabled) {
        for (int i = 0; i < cardContainer.getChildCount(); i++) {
            cardContainer.getChildAt(i).setEnabled(enabled);
        }
    }

    private void endGame() {
        finalPlayerLifeText.setText("あなたのライフ：" + getHearts(playerLife));
        finalEnemyLifeText.setText("相手のライフ：" + getHearts(enemyLife));

        if (playerLife > enemyLife) {
            finalResultText.setText("あなたの勝ち！");
        } else if (playerLife < enemyLife) {
            finalResultText.setText("あなたの負け！");
        } else {
            finalResultText.setText("引き分け！");
        }

        gameScreen.setVisibility(View.GONE);
        resultScreen.setVisibility(View.VISIBLE);
    }
}