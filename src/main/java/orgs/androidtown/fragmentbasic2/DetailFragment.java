package orgs.androidtown.fragmentbasic2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailFragment extends Fragment {


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        init(view);
        return view;
    }

    private TextView textView;
    private void init(View view){
        textView = view.findViewById(R.id.textView);
        // Argument 로 전달된 값 꺼내기
        Bundle bundle = getArguments();
        if(bundle != null) {
            setText(bundle.getString("value"));
        }
    }

    public void setText(String text){
        textView.setText(text);
    } // 메서드로 빼서, 파라미터가 들어오면 자동적으로 텍스트뷰에 설정
}