package com.example.stark.formulizer.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stark.formulizer.Adapters.PageAdapter;
import com.example.stark.formulizer.Fragments.CustomerFragment;
import com.example.stark.formulizer.Fragments.FormulaFragment;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Utilities.PreferenceReader;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{
    PreferenceReader prf;
    boolean isLoggedIn =false;
    private boolean isSearchOpened = false;
    private EditText editSearch;

    CustomerFragment customerFragment;
    FormulaFragment formulaFragment;

    PageAdapter _adapter;
    ViewPager _viewPager;
    MenuItem mSearchaction;

    @BindView(R.id.shared_fab) FloatingActionButton mSharedFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf = new PreferenceReader(MainActivity.this);
        isLoggedIn = !prf.getTOKEN().isEmpty();
        if(!isLoggedIn){
            Intent login = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(login);
            finish();
        }
        customerFragment = new CustomerFragment();
        formulaFragment = new FormulaFragment();
        customerFragment.setRetainInstance(true);
        formulaFragment.setRetainInstance(true);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //View Pager
        _viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(_viewPager);
        //Tab Layout
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(_viewPager);
    }

    private void setupViewPager(final ViewPager viewPager) {
        _adapter = new PageAdapter(getSupportFragmentManager());
            _adapter.addFragment(formulaFragment, getResources().getString(R.string.formula_tab_title));
            _adapter.addFragment(customerFragment, getResources().getString(R.string.customer_tab_title));
        viewPager.setAdapter(_adapter);
        formulaFragment.shareFab(mSharedFab);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        formulaFragment.shareFab(mSharedFab);
                        customerFragment.shareFab(null);
                        break;
                    case 1:
                    default:
                        formulaFragment.shareFab(null);
                        customerFragment.shareFab(mSharedFab);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state){
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mSharedFab.hide();
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        switch (viewPager.getCurrentItem()){
                            case 0:
                                formulaFragment.shareFab(mSharedFab);
                                customerFragment.shareFab(null);
                                break;
                            case 1:
                            default:
                                formulaFragment.shareFab(null);
                                customerFragment.shareFab(mSharedFab);
                                break;
                        }
                        mSharedFab.show();
                        break;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        if(isLoggedIn){
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }
        else{
            menu.getItem(1).setEnabled(false);
            menu.getItem(0).setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchaction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
           case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_login:
                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                return true;
            case R.id.action_logout:
                prf.setTOKEN("");
                Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                return true;
            case R.id.action_search:
                    handleMenuSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar();
        if(isSearchOpened){
            action.setDisplayShowCustomEnabled(false);
            action.setDisplayShowTitleEnabled(true);

            //hides keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(),0);

            //add the search icon in the action bar
            mSearchaction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_18dp));

            isSearchOpened = false;
        }
        else{
            action.setDisplayShowCustomEnabled(true);
            //Custom view in action bar
            action.setCustomView(R.layout.searchbar);
            action.setDisplayShowTitleEnabled(false);

            editSearch = (EditText) action.getCustomView().findViewById(R.id.editSearch);
            editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                   if(actionId == EditorInfo.IME_ACTION_SEARCH){
                       Search(v.getText().toString());
                       return true;
                   }
                    return false;
                }
            });
            editSearch.requestFocus();

            //open the keyboard focused in the editSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editSearch,InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchaction.setIcon(getResources().getDrawable(R.drawable.ic_clear_white_18dp));

            isSearchOpened = true;
        }
    }

    protected void Search(String term){
        if(!term.isEmpty()){
            if(_adapter.getItem(_viewPager.getCurrentItem()) instanceof FormulaFragment){
                formulaFragment.searchFormulaPersonal(term,1);
            }
            else if(_adapter.getItem(_viewPager.getCurrentItem()) instanceof CustomerFragment){
                customerFragment.searchCustomer(term,1);
            }

        }
    }
}
