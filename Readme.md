# ADS04 Android

## 수업 내용
- Fragment 간 데이터 전달 학습

## Code Review

### MainActivity

```Java
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
                == Configuration.ORIENTATION_PORTRAIT){ // 세로모드일때 프래그먼트를 삽입
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
                == Configuration.ORIENTATION_PORTRAIT){ // 화면이 가로로 바뀔때, 세로일때의 값을 bundle로 전달해줌.
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

### ListFragment

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        // 각 각의 Fragment 레이아웃 중복 없이 사용하고 싶다면 savedInstanceState를 false로 사용


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

### DetailFragment

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
    } // 메서드로 빼서, 파라미터가 들어오면 자동적으로 텍스트뷰에 설정
}
```

### CustomAdapter

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


## 보충설명


### 프래그먼트 사용시 주의 사항

- <background> 속성 설정 : 겹쳐서 보여질 수 있음.
- <clickable> 속성 설정 : Fragment는 Activity와 다르게 Click 시 밑에 쌓여있던 버튼들이 클릭되기 때문에 Clickable = true로 해줘야 함.
- addToBackStack(null) : 트랜잭션을 Fragment 트랜잭션의 백 스택에 추가할 수있기에, 나중에 이전 Fragment상태로 되돌아 갈 수 있다. 
- ※ 프래그먼트로부터 콜백이벤트를 받기 위해서 호스트 액티비티는 반드시 프래그먼트 클래스 안에 정의되어 있는 인터페이스를 구현해야함.

### Fragmnet 관리

> FragementManager을 통해 할 수 있는 일
  
  - Activity내에 존재하는 Fragment를 findFragmentById()로 가져오거나 findFragmentByTag()로 가져올 수 있습니다. 
  - popBackStack()을 사용하여 Fragment를 백스택에서 꺼낼 수 있습니다. 
  - 백스택 변경 내용이 있는지 확인하기 위해 addOnBackStackChangedListener()로 리스너를 등록할 수 있습니다. 

### Activity와 통신 

> Fragment에서 activity의 인스턴스에 엑세스 하려면 getActivity()를 사용합니다.
   - View listView = getActivity().findViewById(R.id.list);
> activity에서도 fragment 안의 메서드를 호출할 수 있습니다. 이 때 FragmentManager을 사용하여 Fragment에 대한 참조를 가져와야 합니다. 
  - ExampleFragment fragment = (ExampleFragment) getFragmentManager().findFragmentById(R.id.example_fragment);

### 프래그먼트간 데이터 전송

- 기본적으로 프래그 먼트간 직접적인 데이터 전송은 불가능기에 바인딩되어 있는 hostActivity를 활용해야함.
- 데이터 전달하는 예제 코드
```Java
//Activity에서 전달 시
MyFragment myFragment = new MyFragment();
Bundle bundle = new Bundle();
bundle.putString("key", "전달할 메세지");
myFragment.setArguments(bundle);
```
```Java
//Fragment에서 받을 시
Bundle bundle = getArguments();
String message = bundle.getString("key");
```

#### parcelable 객체를 FragmentArgument로 전달하는 방법

```Java
public class MyData implements Parcelable{
 
        public String name;
        public String address;
 
        protected MyData(Parcel in) {
            name = in.readString();
            address = in.readString();
        }
 
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(address);
        }
 
        @Override
        public int describeContents() {
            return 0;
        }
 
        public static final Creator<MyData> CREATOR = new Creator<MyData>() {
            @Override
            public MyData createFromParcel(Parcel in) {
                return new MyData(in);
            }
 
            @Override
            public MyData[] newArray(int size) {
                return new MyData[size];
            }
        };
    }
```

```Java
public class MyFragment extends Fragment{
    
     public static Fragment newInstance(MyData data) {
        Fragment f = new MyFragment();
        Bundle b = new Bundle();
        b.putParcelable(MyData.class.getName(), data);
        f.setArguments(b);
        return f;
    }
    
}

```
### bundle이란?

```Java
Bundle은 클래스이다.즉, 여러가지의 타입의 값을 저장하는 Map 클래스이다.
자바에는 구조체가 없어서, 클래스로 이용하므로, 다른 언어의 구조체라고 생각하면 될 것 같다.
예를 들면 string 값을 Bundle 클래스에 Mapping(대응, 변환)하는 것이다.
기본타입인 int, double, long, String 부터 FloatArray, StringArrayList, Serializable, Parcelable 까지 구현한다.
(Serializable(객체 직렬화)는 객체를 바이트로 저장하는 자바의 인터페이스이고,Parcelable는 안드로이드에서 만든 것이다.)
Android에서는 Activity간에 데이터를 주고 받을 때 Bundle 클래스를 사용하여 데이터를 전송 할 수 있음.

bundle의 다른 용도는 아래와 같이 Activity를 생성할 때 아래와 같이 Bundle savedInstanceState 객체를 가지고 와서,
액티비티를 중단할 때 savedInstanceState 메서드를 호출하여 임시적으로 데이터를 저장한다.
즉 전에 저장된 데이터가 있으면, 그 데이터를 가지고 Activity를 다시 생성한다.
public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
```

> bundle은 상태/값 등을 저장하기 위한 객체이고
> intent는 저장이 아닌 전달하는 수단으로의 객체


### 출처

- 출처: http://nexthops.tistory.com/24 [꿀단지]
- 출처: http://devilbbong.tistory.com/9 [BBong's Story]
- 출처: http://family-gram.tistory.com/60 [FamilyGram]

## TODO

- callback의 흐름 및 context가 하는 일 및 기능 더 찾아보기
- parcelable 개념 및 사용법 익히기
- callbyValue, callbyReference 공부하기
- bundle로 데이터 전송하는 것과 인텐트로 전송하는 것의 차이

## Retrospect

- 프래그먼트, 그리고 프래그먼트와 액티비티 통신을 위한 interface 구현, 데이터 전송 시에 bundle을 사용하는것, 액티비티와 프래그먼트의 생명주기의 차이 등 
복잡한 내용이 많아졌음.
- 중요한 내용이 많이 담긴 예제이므로 추후 지속적으로 복습해야함.

## Output
- 생략

