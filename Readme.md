# Fragment2

## 프래그먼트간 데이터 전달 실습 (리싸이클러 뷰를 활용)

### 클래스 구성 및 레이아웃 구성

- main클래스 및 레이아웃
- detail프래그먼트클래스 및 레이아웃
- recyclerViewAdapter 클래스와 itemlist레이아웃
- list프래그먼트클래스 및 레이아웃

#### main
```Java
public class MainActivity extends AppCompatActivity
        implements ListFragment.Callback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
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
    public void goDetail(String value) {
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
```

- 메인레이아웃인 Frame레이아웃안에 다음과 같은 코드로 프래그먼트 담아줌
```Java
private void initFragment(){ // 프래그먼트 더하기
    getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.container, new ListFragment())
            .commit();
}
```
- 액티비티사엥 연결을 해주려면, 프래그먼트매니저와 beginTransaction을 거쳐야 함.

- 호스트액티비티는 findFragmentById()를 통해 프래그먼트 인스턴스를 참조함으로써 프레그먼트에게 메세지를 전달할 수 있음. 그런 다음, 직접적으로 프레그먼트의 public 메소드들을 호출

- 화면이 가로로 바뀔때, 세로일때의 값을 bundle로 전달해줌.
```Java
Bundle bundle = new Bundle();
bundle.putString("value", value);
// 프래그먼트로 값 전달하기
detailFragment.setArguments(bundle);
```

#### detailFragment

```Java
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
    }
}
```
- 메인에서 보낸 번들객체를 받음
```Java
private void init(View view){
    textView = view.findViewById(R.id.textView);
    // Argument 로 전달된 값 꺼내기
    Bundle bundle = getArguments();
    if(bundle != null) {
        setText(bundle.getString("value"));
    }
}
```

#### ListFragment

```Java
public class ListFragment extends Fragment {
    Context context;
    Callback callback;
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof Callback){
            callback = (Callback) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        init(view);
        return view;
    }

    RecyclerView recyclerView;
    CustomAdapter adapter;
    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        // 데이터를 임의로 생성해서 넘겨준다
        List<String> data = new ArrayList<>();
        for(int i=0 ; i<100 ; i++){
            data.add("temptData "+i);
        }
        adapter = new CustomAdapter(context, callback, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public interface Callback {
        public void goDetail(String value);
    }

}
```


- Fragment는 여러 Activity에서 사용될 수 있으므로 Activity에 독립적으로 구현
- Fragment는 Activity가 아니기 때문에 context를 가지지 않음
- Fragment는 getActivity() 메소드로 Attach 되어 있는 Activity를 가져올 수 있음.
- Fragment 내에서 발생하는 이벤트를 Activity와 공유하기 위해서는 Fragment 내에서 이벤트 콜백 인터페이스를 정의하고 Activity에서 그 인터페이스를 구현
- Fragment는 본인의 라이프사이클(lifecycle) 메소드인 onAttach()에서 인터페이스를 붙잡도록 구현

#### 리싸이클러아답터

```Java
public class CustomAdapter extends RecyclerView.Adapter<Holder>{
    Context context;
    ListFragment.Callback callback;
    List<String> data;
    public CustomAdapter(Context context, ListFragment.Callback callback, List<String> data){
        this.context = context;
        this.callback = callback;
        this.data = data;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new Holder(view, callback);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}

class Holder extends RecyclerView.ViewHolder{
    private TextView textView;
    public Holder(View itemView, final ListFragment.Callback callback) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.goDetail(textView.getText().toString());
            }
        });
    }
    public void setText(String text){
        textView.setText(text);
    }
}

```
- 홀더에서 클릭시 콜백으로 인터페이스 구현
- 리싸이클러뷰 홀더에 콜백인터페이스 객체를 구현하고, 이벤트시에 콜백 메서드를 구현
- 콜백같은 경우 여러 클래스에서 공통적으로 필요하므로, 인터페이스로 빼놓았다고 이해. 개념 보충이 필요함

- 콜백이란, 예를 들어, A가 B를 호출하여 B가 작업을 수행하다 어떤 시점에서 다시 B는 A를 호출, 그때 A가 정해놓은 작업을 수행 하는 것
