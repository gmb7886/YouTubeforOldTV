package com.marinov.youtube;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.marinov.youtube.R;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "https://youtube.com/tv";
    // Define o User Agent para SmartTV LG
    private static final String USER_AGENT = "Mozilla/5.0 (Web0S; Linux/SmartTV) AppleWebKit/538.2 (KHTML, like Gecko) Large Screen Safari/538.2 LG Browser/7.00.00(LGE; WEBOS2; 04.06.25; 1; DTV_W15U); webOS.TV-2015; LG NetCast.TV-2013 Compatible (LGE, WEBOS2, wireless)";

    private GeckoView geckoView;
    private GeckoSession geckoSession;
    private GeckoRuntime runtime;
    private boolean isGeckoFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ajusta padding conforme as system bars
        View mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout,
                (v, insets) -> {
                    Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom);
                    return insets;
                }
        );

        setupGeckoView();
    }

    private void setupGeckoView() {
        FrameLayout container = findViewById(R.id.webview_container);
        geckoView = new GeckoView(this);
        container.addView(geckoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Configurações do runtime
        GeckoRuntimeSettings.Builder runtimeSettingsBuilder = new GeckoRuntimeSettings.Builder();

        // Inicializa o GeckoRuntime
        runtime = GeckoRuntime.create(this, runtimeSettingsBuilder.build());

        // Configurações da sessão com User Agent
        GeckoSessionSettings.Builder sessionSettingsBuilder = new GeckoSessionSettings.Builder()
                .userAgentOverride(USER_AGENT);

        // Cria a sessão
        geckoSession = new GeckoSession(sessionSettingsBuilder.build());

        // Abre a sessão com o runtime
        geckoSession.open(runtime);

        // Associa a sessão à view
        geckoView.setSession(geckoSession);

        // Configuração de fullscreen para vídeos
        geckoSession.setContentDelegate(new GeckoSession.ContentDelegate() {
            @Override
            public void onFullScreen(@NonNull GeckoSession session, boolean fullScreen) {
                runOnUiThread(() -> {
                    isGeckoFullScreen = fullScreen;
                    if (fullScreen) {
                        // Usar flags compatíveis com API 14+
                        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

                        // Adicionar flag sticky apenas se disponível
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                        }

                        getWindow().getDecorView().setSystemUiVisibility(flags);
                    } else {
                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                });
            }
        });

        // Carrega a URL se houver conexão
        if (isNetworkAvailable()) {
            geckoSession.loadUri(URL);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isGeckoFullScreen) {
                geckoSession.exitFullScreen();
                return true;
            }
            // Sempre tentar voltar, mesmo sem verificar canGoBack()
            geckoSession.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (geckoSession != null) {
            geckoSession.setActive(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (geckoSession != null) {
            geckoSession.setActive(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (geckoSession != null) {
            geckoSession.close();
            geckoSession = null;
        }
        if (geckoView != null) {
            ViewGroup parent = (ViewGroup) geckoView.getParent();
            if (parent != null) {
                parent.removeView(geckoView);
            }
            geckoView = null;
        }
        if (runtime != null) {
            runtime.shutdown();
            runtime = null;
        }
        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        // Método compatível com todas as versões do Android
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}