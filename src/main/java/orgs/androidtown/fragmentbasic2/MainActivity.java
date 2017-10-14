package orgs.androidtown.fragmentbasic2;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
        implements ListFragment.Callback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) // 이유는 각자 검색
            return;

        init();
    }

    private void init(){
        if(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT){ // 현재 레이아웃? 세로체크
            initFragment();
        }
    }

    private void initFragment(){ // 프래그먼트 더하기
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ListFragment())
                .commit();
    }

    @Override
    public void goDetail(String value) { // 이 콜백 부분 고민이 필요
        if(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT){ // 현재 레이아웃? 세로체크
            // 디테일 프래그먼트를 화면에 보이면서 값을 전달
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("value", value);
            // 프래그먼트로 값 전달하기
            detailFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }else{
            // 현재 레이아웃에 삽입되어 있는 프래그먼트를 가져온다
            DetailFragment fragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            // 만들어놓은 함수를 호출해서 값을 세팅한다.
            if(fragment != null) {
                fragment.setText(value);
            }
        }
    }
}