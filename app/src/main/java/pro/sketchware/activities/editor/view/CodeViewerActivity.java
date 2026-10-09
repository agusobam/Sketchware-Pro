package pro.sketchware.activities.editor.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.besome.sketch.lib.base.BaseAppCompatActivity;

import a.a.a.Lx;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.databinding.ActivityCodeViewerBinding;
import pro.sketchware.utility.EditorUtils;
import pro.sketchware.utility.UI;

public class CodeViewerActivity extends BaseAppCompatActivity {

    public static final String SCHEME_XML = "xml";
    public static final String SCHEME_JAVA = "java";

    private static final int MENU_SEARCH = 1;

    private ActivityCodeViewerBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        enableEdgeToEdgeNoContrast();
        super.onCreate(savedInstanceState);

        binding = ActivityCodeViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        var code = getIntent().getStringExtra("code");
        var scheme = getIntent().getStringExtra("scheme");
        var scId = getIntent().getStringExtra("sc_id");

        binding.toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        binding.toolbar.setSubtitle(scId);
        binding.toolbar.getMenu()
                .add(Menu.NONE, MENU_SEARCH, Menu.NONE, "Search")
                .setIcon(R.drawable.ic_mtrl_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == MENU_SEARCH) {
                beginSearch();
                return true;
            }
            return false;
        });

        binding.editor.setTypefaceText(EditorUtils.getTypeface(this));
        binding.editor.setTextSize(14);
        binding.editor.setText(Lx.j(code, false));
        binding.editor.setEditable(false);
        binding.editor.setWordwrap(false);
        loadColorScheme(scheme);

        UI.addSystemWindowInsetToPadding(binding.appBarLayout, true, true, true, false);
        UI.addSystemWindowInsetToMargin(binding.editor, true, false, true, true);
    }

    private void beginSearch() {
        binding.editor.getSearcher().stopSearch();
        binding.editor.beginSearchMode();
        binding.editor.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            View decor = getWindow().getDecorView();
            View searchSrcText = null;
            int resId = getResources().getIdentifier("search_src_text", "id", "android");
            if (resId != 0) {
                searchSrcText = decor.findViewById(resId);
            }
            if (searchSrcText == null) {
                searchSrcText = decor.findViewById(androidx.appcompat.R.id.search_src_text);
            }
            if (searchSrcText != null) {
                searchSrcText.setFocusableInTouchMode(true);
                searchSrcText.requestFocus();
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchSrcText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 250);
    }

    private void loadColorScheme(String scheme) {
        if (scheme.equals(SCHEME_XML)) {
            EditorUtils.loadXmlConfig(binding.editor);
        } else {
            EditorUtils.loadJavaConfig(binding.editor);
        }
    }
}
