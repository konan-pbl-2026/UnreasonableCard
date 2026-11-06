
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
    private boolean isSpecialMode = false;

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

    private LinearLayout startScreen;
    private LinearLayout helpSelectScreen;
    private LinearLayout helpNormalScreen;
    private LinearLayout helpSpecialScreen;

    // 各ゲーム画面
    private LinearLayout normalGameScreen;
    private LinearLayout specialGameScreen;

    private LinearLayout pauseScreen;
    private LinearLayout resultScreen;

    private final String[] hands = {"✊", "✌️", "✋"};
    private final Random random = new Random();

    // ===== 特殊カード管理 =====

    // 特殊カードの番号
    // 0 = specialCard1
    // 1 = specialCard2
    // 2 = specialCard3

    private ArrayList<Integer> playerSpecialCards = new ArrayList<>();
    private ArrayList<Integer> enemySpecialCards = new ArrayList<>();

    // 自分が選択している特殊カード
    // -1 = 選択なし
    private int selectedSpecialCard = -1;

    // 特殊カード表示エリア
    private LinearLayout specialCardContainer;

    // 特殊カード名
    private final String[] specialCardNames = {
            "specialCard1",
            "specialCard2",
            "specialCard3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 各画面
        startScreen = findViewById(R.id.startScreen);
        helpSelectScreen = findViewById(R.id.helpSelectScreen);
        helpNormalScreen = findViewById(R.id.helpNormalScreen);
        helpSpecialScreen = findViewById(R.id.helpSpecialScreen);
        // 通常モード画面と特殊モード画面
        normalGameScreen = findViewById(R.id.normalGameLayout);
        specialGameScreen = findViewById(R.id.specialGameLayout);

        pauseScreen = findViewById(R.id.pauseScreen);
        resultScreen = findViewById(R.id.resultScreen);

        // タイトル画面のボタン
        Button normalModeButton = findViewById(R.id.normalModeButton);
        Button specialModeButton = findViewById(R.id.specialModeButton);
        Button helpButton = findViewById(R.id.helpButton);

        // 説明画面のボタン
        Button normalHelpButton = findViewById(R.id.normalHelpButton);
        Button specialHelpButton = findViewById(R.id.specialHelpButton);
        Button helpSelectBackButton = findViewById(R.id.helpSelectBackButton);
        Button normalHelpBackButton = findViewById(R.id.normalHelpBackButton);
        Button specialHelpBackButton = findViewById(R.id.specialHelpBackButton);

        // ゲーム画面の部品
        finalPlayerLifeText = findViewById(R.id.finalPlayerLifeText);
        finalEnemyLifeText = findViewById(R.id.finalEnemyLifeText);
        finalResultText = findViewById(R.id.finalResultText);

        Button resumeButton = findViewById(R.id.resumeButton);
        Button pauseRetryButton = findViewById(R.id.pauseRetryButton);
        Button pauseTitleButton = findViewById(R.id.pauseTitleButton);

        Button retryButton = findViewById(R.id.retryButton);
        Button titleButton = findViewById(R.id.titleButton);

// 通常モード開始
        normalModeButton.setOnClickListener(v -> {
            isSpecialMode = false;
            startScreen.setVisibility(View.GONE);
            specialGameScreen.setVisibility(View.GONE);
            normalGameScreen.setVisibility(View.VISIBLE);
            setGameViews();
            startGame();
        });

// 特殊モード開始
        specialModeButton.setOnClickListener(v -> {
            isSpecialMode = true;
            startScreen.setVisibility(View.GONE);
            normalGameScreen.setVisibility(View.GONE);
            specialGameScreen.setVisibility(View.VISIBLE);
            setGameViews();
            startGame();
        });

        // 遊び方説明を開く
        helpButton.setOnClickListener(v -> {
            startScreen.setVisibility(View.GONE);
            helpSelectScreen.setVisibility(View.VISIBLE);
        });

        // 通常モードの説明を開く
        normalHelpButton.setOnClickListener(v -> {
            helpSelectScreen.setVisibility(View.GONE);
            helpNormalScreen.setVisibility(View.VISIBLE);
        });

        // 特殊モードの説明を開く
        specialHelpButton.setOnClickListener(v -> {
            helpSelectScreen.setVisibility(View.GONE);
            helpSpecialScreen.setVisibility(View.VISIBLE);
        });

        // 説明選択画面からタイトルへ
        helpSelectBackButton.setOnClickListener(v -> {
            helpSelectScreen.setVisibility(View.GONE);
            startScreen.setVisibility(View.VISIBLE);
        });

        // 通常モード説明から説明選択へ
        normalHelpBackButton.setOnClickListener(v -> {
            helpNormalScreen.setVisibility(View.GONE);
            helpSelectScreen.setVisibility(View.VISIBLE);
        });

        // 特殊モード説明から説明選択へ
        specialHelpBackButton.setOnClickListener(v -> {
            helpSpecialScreen.setVisibility(View.GONE);
            helpSelectScreen.setVisibility(View.VISIBLE);
        });

// ゲームに戻る
        resumeButton.setOnClickListener(v -> {
            pauseScreen.setVisibility(View.GONE);
            if (isSpecialMode) {
                specialGameScreen.setVisibility(View.VISIBLE);
            } else {
                normalGameScreen.setVisibility(View.VISIBLE);
            }
        });

        // ポーズ中に最初から
        pauseRetryButton.setOnClickListener(v -> {
            pauseScreen.setVisibility(View.GONE);
            if (isSpecialMode) {
                specialGameScreen.setVisibility(View.VISIBLE);
            } else {
                normalGameScreen.setVisibility(View.VISIBLE);
            }
            setGameViews();
            startGame();
        });

// ポーズ中にタイトルに戻る
        pauseTitleButton.setOnClickListener(v -> {
            pauseScreen.setVisibility(View.GONE);
            normalGameScreen.setVisibility(View.GONE);
            specialGameScreen.setVisibility(View.GONE);
            startScreen.setVisibility(View.VISIBLE);
        });

        // 対戦終了後にもう一度
        retryButton.setOnClickListener(v -> {
            resultScreen.setVisibility(View.GONE);
            if (isSpecialMode) {
                specialGameScreen.setVisibility(View.VISIBLE);
            } else {
                normalGameScreen.setVisibility(View.VISIBLE);
            }
            setGameViews();
            startGame();
        });

// 対戦終了後にタイトルに戻る
        titleButton.setOnClickListener(v -> {
            resultScreen.setVisibility(View.GONE);
            normalGameScreen.setVisibility(View.GONE);
            specialGameScreen.setVisibility(View.GONE);
            startScreen.setVisibility(View.VISIBLE);
        });

    }

    // 選択したモードの画面部品を取得する
    private void setGameViews() {

        LinearLayout currentScreen;

        if (isSpecialMode) {
            currentScreen = specialGameScreen;
        } else {
            currentScreen = normalGameScreen;
        }

        playerLifeText = currentScreen.findViewById(R.id.playerLifeText);
        enemyLifeText = currentScreen.findViewById(R.id.enemyLifeText);
        resultText = currentScreen.findViewById(R.id.resultText);

        battlePlayerCard = currentScreen.findViewById(R.id.battlePlayerCard);
        battleEnemyCard = currentScreen.findViewById(R.id.battleEnemyCard);

        cardContainer = currentScreen.findViewById(R.id.cardContainer);
        enemyCardContainer = currentScreen.findViewById(R.id.enemyCardContainer);

        pauseButton = currentScreen.findViewById(R.id.pauseButton);

        if (isSpecialMode) {
            specialCardContainer =
                    currentScreen.findViewById(R.id.specialCardContainer);
        }
        // ポーズボタン
        pauseButton.setOnClickListener(v -> {
            if (isSpecialMode) {
                specialGameScreen.setVisibility(View.GONE);
            } else {
                normalGameScreen.setVisibility(View.GONE);
            }
            pauseScreen.setVisibility(View.VISIBLE);
        });
    }


    // 特殊カードを配布する
    private void distributeSpecialCards() {

        playerSpecialCards.clear();
        enemySpecialCards.clear();

        selectedSpecialCard = -1;

        // 3種類の特殊カードを用意
        ArrayList<Integer> specialDeck = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            specialDeck.add(i);
        }

        // シャッフル
        Collections.shuffle(specialDeck);

        // 自分に1枚
        playerSpecialCards.add(specialDeck.get(0));

        // 相手に1枚（自分とは重複しない）
        enemySpecialCards.add(specialDeck.get(1));

        // specialDeck.get(2) は未配布
    }

    // 自分の特殊カードを表示する
    private void updateSpecialCards() {
        if (specialCardContainer == null) {
            return;
        }

        specialCardContainer.removeAllViews();

        if (!isSpecialMode) {
            specialCardContainer.setVisibility(View.GONE);
            return;
        }

        specialCardContainer.setVisibility(View.VISIBLE);

        // 所持している特殊カードを表示
        for (int card : playerSpecialCards) {

            Button specialButton = new Button(this);

            specialButton.setText(specialCardNames[card]);
            specialButton.setTextSize(10);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            55
                    );

            params.setMargins(2, 3, 2, 3);
            specialButton.setLayoutParams(params);

            // 選択中のカードを黄色にする
            if (selectedSpecialCard == card) {
                specialButton.setBackgroundColor(0xFFFFD54F);
            } else {
                specialButton.setBackgroundResource(
                        R.drawable.card_background
                );
            }

            specialButton.setOnClickListener(v -> {

                if (selectedSpecialCard == card) {
                    selectedSpecialCard = -1;
                } else {
                    selectedSpecialCard = card;
                }

                updateSpecialCards();
            });

            specialCardContainer.addView(specialButton);
        }

        // 特殊カードを使わない選択肢
        Button noSpecialButton = new Button(this);

        noSpecialButton.setText("使わない");
        noSpecialButton.setTextSize(11);

        LinearLayout.LayoutParams noParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        45
                );

        noParams.setMargins(2, 3, 2, 3);
        noSpecialButton.setLayoutParams(noParams);

        noSpecialButton.setOnClickListener(v -> {

            selectedSpecialCard = -1;

            updateSpecialCards();
        });

        specialCardContainer.addView(noSpecialButton);
    }

    // 特殊カードの操作を有効・無効にする
    private void setSpecialCardsEnabled(boolean enabled) {
        if (specialCardContainer == null) {
            return;
        }
        for (int i = 0;
             i < specialCardContainer.getChildCount();
             i++) {
            specialCardContainer.getChildAt(i).setEnabled(enabled);
        }
    }

    // ゲーム開始
    private void startGame() {

        playerLife = 3;
        enemyLife = 3;

        int cardCountPerHand = 5;

        ArrayList<Integer> deck = new ArrayList<>();

        for (int i = 0; i < cardCountPerHand; i++) {
            deck.add(0);
            deck.add(1);
            deck.add(2);
        }

        Collections.shuffle(deck);

        // 特殊カードの初期化
        if (isSpecialMode) {
            distributeSpecialCards();
        } else {
            playerSpecialCards.clear();
            enemySpecialCards.clear();
            selectedSpecialCard = -1;
        }

        playerHand = new ArrayList<>(
                deck.subList(0, cardCountPerHand)
        );

        enemyHand = new ArrayList<>(
                deck.subList(cardCountPerHand, cardCountPerHand * 2)
        );

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

        // 今回使用する特殊カード
        int playerSpecialUsed = -1;
        int enemySpecialUsed = -1;

        if (isSpecialMode) {

            // 自分が選択した特殊カードを使用
            if (selectedSpecialCard != -1) {

                playerSpecialUsed = selectedSpecialCard;

                playerSpecialCards.remove(
                        Integer.valueOf(selectedSpecialCard)
                );

                selectedSpecialCard = -1;
            }

            // 相手は特殊カードを使うかランダムで決定
            if (!enemySpecialCards.isEmpty()
                    && random.nextBoolean()) {

                enemySpecialUsed = enemySpecialCards.remove(0);
            }

            updateSpecialCards();
            setSpecialCardsEnabled(false);
        }

        // 相手のじゃんけんの手を決定
        int enemyIndex = random.nextInt(enemyHand.size());
        int enemyChoice = enemyHand.get(enemyIndex);

        battlePlayerCard.setText(hands[playerChoice]);

        battlePlayerCard.setBackgroundResource(
                R.drawable.card_background
        );

        battlePlayerCard.setVisibility(View.VISIBLE);

        battleEnemyCard.setText("");

        battleEnemyCard.setBackgroundResource(
                R.drawable.enemy_card_background
        );

        battleEnemyCard.setVisibility(View.VISIBLE);

        resultText.setText("カードをセット！ 相手が選んでいます...");

        // 特殊カードの使用情報も勝敗判定に渡す
        final int finalPlayerSpecial = playerSpecialUsed;
        final int finalEnemySpecial = enemySpecialUsed;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            battleEnemyCard.setText(hands[enemyChoice]);

            battleEnemyCard.setBackgroundResource(
                    R.drawable.card_background
            );

            resolveJanken(
                    playerChoice,
                    enemyChoice,
                    enemyIndex,
                    finalPlayerSpecial,
                    finalEnemySpecial
            );

        }, 1000);
    }

    // じゃんけんの勝敗判定
    // じゃんけんの勝敗判定
    private void resolveJanken(
            int playerChoice,
            int enemyChoice,
            int enemyIndex,
            int playerSpecialUsed,
            int enemySpecialUsed) {

        playerHand.remove(Integer.valueOf(playerChoice));
        enemyHand.remove(enemyIndex);

        String message = "あなた：" + hands[playerChoice]
                + " / 相手：" + hands[enemyChoice] + "\n";

        // 0 = あいこ
        // 1 = プレイヤー勝利
        // -1 = 相手勝利
        int winner = 0;

        if (playerChoice == enemyChoice) {

            message += "あいこ！ライフは変化なし";

        } else if ((playerChoice - enemyChoice + 3) % 3 == 2) {

            enemyLife--;
            winner = 1;

            message += "あなたの勝ち！相手のライフ -1";

        } else {

            playerLife--;
            winner = -1;

            message += "あなたの負け！自分のライフ -1";
        }

        // 使用した特殊カードを表示
        if (playerSpecialUsed != -1) {
            message += "\nあなたが使用：" +
                    specialCardNames[playerSpecialUsed];
        }

        if (enemySpecialUsed != -1) {
            message += "\n相手が使用：" +
                    specialCardNames[enemySpecialUsed];
        }

        // specialCard1の効果
        // どちらかがspecialCard1を使用していた場合
        if (isSpecialMode && winner != 0
                && (playerSpecialUsed == 0
                || enemySpecialUsed == 0)) {

            if (winner == 1) {

                // 自分が勝利
                playerLife = Math.min(3, playerLife + 1);

                message += "\n特殊効果発動！あなたのライフ＋1";

            } else {

                // 相手が勝利
                enemyLife = Math.min(3, enemyLife + 1);

                message += "\n特殊効果発動！相手のライフ＋1";
            }
        }

        resultText.setText(message);

        updateScreen();

        pauseButton.setEnabled(true);

        // ライフが0になった場合
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

        // 手札がなくなった場合
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

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(130, 180);

            params.setMargins(8, 0, 8, 0);
            cardButton.setLayoutParams(params);

            cardButton.setOnClickListener(v -> playJanken(cardType));

            cardContainer.addView(cardButton);
        }

        enemyCardContainer.removeAllViews();

        for (int i = 0; i < enemyHand.size(); i++) {

            View enemyCardView = new View(this);
            enemyCardView.setBackgroundResource(
                    R.drawable.enemy_card_background
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(130, 180);

            params.setMargins(8, 0, 8, 0);
            enemyCardView.setLayoutParams(params);

            enemyCardContainer.addView(enemyCardView);
        }
        // 特殊カード表示を更新
        updateSpecialCards();
    }

    // ライフをハートで表示
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

    // カードの有効・無効を切り替える
    private void setCardsEnabled(boolean enabled) {

        for (int i = 0; i < cardContainer.getChildCount(); i++) {
            cardContainer.getChildAt(i).setEnabled(enabled);
        }
    }

    // 対戦終了
    private void endGame() {

        finalPlayerLifeText.setText(
                "あなたのライフ：" + getHearts(playerLife)
        );

        finalEnemyLifeText.setText(
                "相手のライフ：" + getHearts(enemyLife)
        );

        if (playerLife > enemyLife) {
            finalResultText.setText("あなたの勝ち！");
        } else if (playerLife < enemyLife) {
            finalResultText.setText("あなたの負け！");
        } else {
            finalResultText.setText("引き分け！");
        }

// 使用中のゲーム画面を非表示にする
        if (isSpecialMode) {
            specialGameScreen.setVisibility(View.GONE);
        } else {
            normalGameScreen.setVisibility(View.GONE);
        }
        resultScreen.setVisibility(View.VISIBLE);
    }
}
