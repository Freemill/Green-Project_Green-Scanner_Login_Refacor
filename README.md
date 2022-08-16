# Login 기능 Refactoring & Deep dive

이 Repositroy의 목적은 GbcCollector의 로그인 기능을 Refactoring함과 동시에 로그인 기능에 관한 기술들을 Deep dive하기 위함이다. 

Web 기술에 집중을 하기 위해 DB는 메모리로 사용한다. (향후 DB관련 오류학습을 위해 변경 예정)

로그인 기술을 사용, 구현하되, 기술이 내부에서 어떻게 작동하는지를 알기 위해 발전 단계별로 package를 나눠 구현해 나간다. 

향후 참고용 template으로 활용하기 위해 설명은 최대한 자세히 한다.

처음 package는 내부에서 사용하는 logic을 위한 domain과 사용자에게 서비스를 제공할 때 필요한 web으로 나눈다.

학습의 출처는 ***김영한 스프링 MVC 2편***으로 하고 실습 예제는 개인의 다른 템플릿으로 진행한다.



## Day 1

---



<img src="https://user-images.githubusercontent.com/76586084/184356747-ef3e60df-f590-46de-a5c3-44f914d02e1b.png" alt="image" style="zoom: 80%;" />

package의 구성 

MVC pattern으로 구현하고 향후 확장성을 고려하여 Interface를 활용했다.


### IndexPage
<img src="https://user-images.githubusercontent.com/76586084/184359227-9cf1eb57-0467-4df8-86e7-074b3c26f626.png" alt="image" style="zoom: 23%;" />


### memberInsertPage
<img src="https://user-images.githubusercontent.com/76586084/184360117-c2b33092-5d49-4421-a360-d694f9cb9b39.png" alt="image"/>

로그인 학습에 필요한 Page를 우선 구현한다. 기존 미흡하게 구현되었던 Thymeleaf 기능을 조금 Refactoring 하였다.

##### 1.

![image](https://user-images.githubusercontent.com/76586084/184359262-701b5392-cb94-4b18-968e-0d3b6b0dd73b.png)

> root 주소에서 @GetMapping을 활용해 Index page를 server 딴에서 전송하여 Thymeleaf 기능을 지원하게 하였다.



##### 2.

![image](https://user-images.githubusercontent.com/76586084/184359288-adac345b-7356-472d-83b0-06c7fd06d33f.png)

> RESTFUL URI활용하고 th:field 기능을 활용하기 위해 Model 객체를 생성하고 넣어주었다.



## Day 2

---

##### Post 객체 확인

![image](https://user-images.githubusercontent.com/76586084/184472917-f2aa1a50-0ff6-4ac8-86e8-7c9a945d3444.png)

> memberInsertForm 페이지가 잘 연결되있는지 확인하기 위해 ResponseBody와 Log기능을 활용해보았다. (잘 넘어옴)







## Day 3

---

검증 오류 저장소와 검증 로직을 개발해보았다.

![image](https://user-images.githubusercontent.com/76586084/184534610-80beb886-4e1f-43c4-8f61-c809052a7536.png)



이중 emailTypeCheck과 nicknameDuplicateCheck의 메서드를 간단히 설명하면,

1. emailTypeCheck의 경우

![image](https://user-images.githubusercontent.com/76586084/184534622-3ff02a4a-1495-42cc-9560-3a4e3dd741d4.png)

위와같이 정규표현식과 Pattern class를 활용하였다. Pattern.compile()로 주어진 정규표현식으로부터 패턴을 만들고 matcher()를 활용해 패턴이 틀릴경우 True를 반환하는 로직이다. 



2. nicknameDuplicateCheck의 경우

우선 MemoryMemberRepository에

![image](https://user-images.githubusercontent.com/76586084/184534761-06ab8339-a4ad-40ae-aba0-79cc8cf8af5d.png)

findByNickName을 만든 후 이것을 MemberServiceImpl에서 활용했다.

![image](https://user-images.githubusercontent.com/76586084/184534769-a5a15935-48a0-4f17-ace0-418ccc1aa07f.png)

위 로직의 경우 저장소에 닉네임이 존재하지 않으면 False를 반환하는 method이다.

이 로직의 경우 단위 Test를 해보았다.

![image](https://user-images.githubusercontent.com/76586084/184534787-50fe9414-6e49-48b3-be70-b92a7686697d.png)

잘 작동한다.



또한, Filed error를 넘어 Global error를 관리하기 위해 password와 passwordConfirm이 다를 경우를 상정해 보고 Global error를 만들어 보았다.

![image](https://user-images.githubusercontent.com/76586084/184534915-9b7ca345-c445-4fcc-9f3e-656832bb5ddf.png)



만들어진 프로젝트로 실행을 해보았다.

```java
2022-08-14 20:02:07.301  INFO 8296 --- [nio-8080-exec-1] c.g.g.l.w.v.ValidationMemberController   : errors = {privacyCheck=개인정보처리방침 동의는 필수입니다., password=비밀번호 입력은 필수입니다., termsCheck=이용약관 동의는 필수입니다., passwordConfirm=비밀번호 확인 입력은 필수입니다., nickname=중복된 닉네임 입니다., userEmail=이메일 형식에 맞춰 입력해주세요. ex) abcdefg@Naver.com, Name=이름 입력은 필수입니다.}
```

Log가 의도한대로 들어오는것을 확인할 수 있었다.



이제 화면에 errors들을 표시해 보았다.

##### Field-error

```html
<div style="margin-bottom:9px; margin-top:16px;"
							 th:classappend="${errors?.containsKey('name')} ? 'field-error' : _"
							 class="signup-name">이름</div>
                                 
<div class="field-error" th:if="${errors?.containsKey('name')}" th:text="${errors['name']}">
							이름
						</div>
```

##### Global-error

```html
<div th:if="${errors?.containsKey('globalError')}">
							<p class = "field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
						</div>
```



위를 적용한 화면은 아래와 같다.



![image](https://user-images.githubusercontent.com/76586084/184538152-58ed97c0-c7b8-4cb2-98c8-54c6b6ac267a.png)







## Day 4

---

현재까지는 검증 오류 처리를 공부해 보았다. 하지만 여기에는 몇가지 문제점들이 있다.

- 뷰 템플릿에서 중복 처리가 많다.
- 타입 오류 처리가 안되다. 현재까지는 모든 Field가 String으로 되어있어 확인할 수 없었지만 Field가 Integer인 경우 String타입을 처리할 수 없다. (이 문제를 학습하기 위해 아래에서 페이지 수정)
- 예를들어, Integer Field에 String Field를 입력하는 것처럼 타입 오류가 발생해도 고객이 입력한 문자를 화면에 남겨야 한다. 만약 컨트롤러가 호출된다고 가정해도 Iterger에 String을 보관할 수가 없다. 결국 문자는 바인딩이 불가능하므로 고객이 입력한 문자가 사라지게 되고, 고객은 본인이 어떤 내용을 입력해서 오류가 발생했는지 이해하기 어렵다.
- 결국 고객이 입력한 값도 어딘가에 별도로 보관 되어야 한다.

지금부터 이 문제들을 스프링이 제공하는 검증 방법으로 해결해 나간다.



타입에러까지 학습에 넣기 위해 ***이름 Field***를 ***번호 Field***로 바꾼다. (String -> Integer)

![image](https://user-images.githubusercontent.com/76586084/184608727-82c69fc5-5bf5-4da7-8be1-ebd56dff565c.png)

기존의 버전을 유지하고

![image](https://user-images.githubusercontent.com/76586084/184610365-ea16b9b5-746e-4ec8-9795-d0f649ec0925.png)

위와같이, V2로 업그레이드 정적 페이지들도 기존에 것은 v1에 보관

![image-20220815182157050](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220815182157050.png)



기존 검증 FiledError 오류로직을 V2로 Refactoring해보았다.

![image](https://user-images.githubusercontent.com/76586084/184621718-ca3e49fa-9570-41cb-915d-db5335902fc7.png)

기존에는 errors를 HashMap으로 만들어 사용했지만 Spring(킹프링)에서 제공하는 BindingResult를 사용하면 그럴 필요가 없다.

또한 위에서

```java
bindingResult.addError(new FieldError("member", "number", "번호 입력은 필수입니다."));
```

코드를 보면 알 수 있겠지만 OjbectName을 바로 찾는것을 볼 수 있다. 이것은 Spring이 @ModelAttribute 뒤에 BindingResult를 넣어주면 BindingResult에 자동으로 앞의 @ModelAddAttribute에서 생성된 객체가 연결되기 때문에 사용할 수 있는것이다.



이번엔 GlobalError를 Refactoring 해보겠다. 아래와 같인 new Object를 활용하면 된다.

![image](https://user-images.githubusercontent.com/76586084/184621757-a7f53c39-90c0-4efa-bf63-f2baeb72354e.png)



이제 V2버전으로 화면에 bindingResult의 error들을 뿌려 보겠다. 그전과 비교해서 보면 아래와 같다.

```html
<!--v1-->
<input type="text" th:field="*{number}"
       th:classappend="${errors?.containsKey('number')} ? 'field-error' : _"
		placeholder="숫자을 입력해주세요." >
<div class="field-error" th:if="${errors?.containsKey('number')}" th:text="${errors['number']}">
	숫자
</div>

<!--V2-->
<input type="text" th:field="*{number}" th:errorclass="field-error"
							   placeholder="숫자를 입력해주세요." >
<div class="field-error" th:errors="*{number}">
	숫자
</div>
```

확연히 코드가 줄은것을 확인할 수 있다. v2 버전에서 알 수 있든이 th:field의 힘은 대단하다! errors중 *{}에 있는 에러를 확인해주고 그 에러가 있다면 errorclass를 표시해주는 기능까지도 제공을 한다!!! :goat: (GOAT)이다.



V2버전의 Global Error도 살펴보자.

```html
<!-- v1 -->
<div th:if="${errors?.containsKey('globalError')}">
	<p class = "field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
</div>

<!__ v2 -->
<div th:if="${#fields.hasGlobalErrors()}">
	<p class = "field-error" th:each= "err : ${#fields.globalErrors()}" th:text="${err}">
    	글로벌 오류 메시지</p>
</div>
```

효과는 대단했다! (each를 사용)



##### BindingResult

- 스프링이 제공하는 검증 오류를 보관하는 객체이다. 검증 오류가 발생하면 여기에 보관한다.
- ***BindingResult*** 가 있으면 ***@ModelAttribute***에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다. :electric_plug:
  - BindingResult가 있으면, 오류 정보('FieldError')를 'BindingResult'에 담아서 컨트롤러를 정상 호출한다.



타입오류를 핸들링 할 수 있다는 말이다.! 

![image](https://user-images.githubusercontent.com/76586084/184626585-c961e987-ec00-40b0-8c7e-48487ef1993c.png)

요로케 TypeError가 들어옴

그런데 문제는 나의 오류 메시지를 남기지 못하고있다. 이제 다음에는 내가 입력한 오류 메시지를 화면에 출력해보는 학습을 해보자!







## Day 5

---

타입오류를 화면에 남기기 전에 현재 프로젝트는 오류가 발생할 시 기존의 입력 데이터를 화면에 전달하지 못하고 있다.

![image](https://user-images.githubusercontent.com/76586084/184829774-6935a079-67b1-4c5b-8582-0a747403debf.png)

![image](https://user-images.githubusercontent.com/76586084/184829673-17026bd2-0e21-4f34-8d9c-96bc5f274513.png)

오류 메시지는 표시하지만 기존에 입력한 "asdf"는 날아가 버림. :cry:

이 문제를 해결하는 방법은 간단하다.



이 코드를

![image](https://user-images.githubusercontent.com/76586084/184830151-98918440-2ec2-435b-b4b9-acf8c491d6dc.png)

아래와 같이 수정해주면 된다.

![image](https://user-images.githubusercontent.com/76586084/184831720-88f972ce-bbb8-4749-aa0d-eaaa993d1180.png)



##### 파라미터 설명

- ```objectName```  : 오류가 발생한 객체 이름
- ```field``` : 오류 필드
- ```rejectedValue``` : 사용자가 입력한 값(거절된 값)
- ```bindingFailure``` : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- ```codes``` : 메시지 코드
- ```arguments``` :  메시지에 사용하는 인자
- ```defaultMessage``` : 기본 오류 메시지

Object도 유사하게 주 가지 생성자를 제공한다.

![image-20220816171943878](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220816171943878.png)



사용자의 입력 데이터가 컨트롤러의 ***```@ModelAttribute```***에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어렵다. 예를 들어서 숫자에 숫자가 아닌 문자가 입력된다면 가격은 ```Integer``` 타입으로 문자를 보관할 수 있는 방법이 없다. 그래서 오류가 발생한 경우 사용자가 입력 값을 보관하는 별도의 방법이 필요하다. 그리고 이렇게 보관한 사용자 입력 값을 검증 오류 발생시 화면에 다시 출력하면 된다.

```FieldError```는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공한다.



이제부터 본격적으로 오류 코드와 메시지에 대하여 알아보자. :fire::fire: :fireworks:

현재까지 

![image](https://user-images.githubusercontent.com/76586084/184830151-98918440-2ec2-435b-b4b9-acf8c491d6dc.png)

이런식으로 오류 메시지를 생성해 주었다. 하지만 이런 메시지도 한군데에서 모아놓고 관리해주면 상당히 관리하기에도 편하고 사용에도 편리할것 같다!

이것을 할 수 있는 기능이 킹프링에는 존재한다! properties 파일을 활용하면된다.

![image](https://user-images.githubusercontent.com/76586084/184831720-88f972ce-bbb8-4749-aa0d-eaaa993d1180.png)

***```codes```***, ***```arguments```***를 활용하면 된다!  



![image-20220816175216070](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220816175216070.png)

errors.properties를 만들어줌! 

```applications.properties
spring.messages.basename = errors
```

추가해줌!

그리고

![image-20220816185433243](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220816185433243.png)

이렇게 오류 메시지를 추가해 준다.

또한 아래와 같이 오류메시지를 사용하게 Controller또한 Version upgrade를 한다.

![image](https://user-images.githubusercontent.com/76586084/184851469-277425b8-8d70-4fc3-af0a-0ea5bf5a6e89.png)

![image-20220816185527175](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220816185527175.png)

잘 작동하는 것을 확인할 수 있다.

```java
//codes에 아래와 같이 String[]{}같이 넣어주는 이유는 default를 이용하거나 오류메시지가 없을 때를 대비해 여러개의 오류메시지를 넣을 수 있기 때문이다.
bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, new String[]{"duplicated.member.nickName"}, null, null));
```



여기서 또 Version up을 해보자 .

지금까지 ***```FieldError```***와 ***```ObjectError```***를 다루기 너무 번거로웠다.
또한 오류코드도 조금 더 자동화 시킬 수 있을것 같다.



컨트롤러에서 ***```BindingResult```***는 검증해야 할 객체인 ***```target```***바로 다음에 온다. 따라서  ***```BindingResult```***는 이미 본인이 검증해야 할 객체인 ***```target```***을 알고 있다.



##### ***```rejectValue()```***, ***```reject()```*** 

그리고 ***```BindingResult```***가 제공하는 ***```rejectValue()```***, ***```reject()```*** 를 사용하면 ***```FieldError```***, ***```ObjectError```***를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.

***```rejectValue()```***, ***```reject()```*** 를 사용해서 기존 코드를 단순화 해보자. :shallow_pan_of_food:

![image](https://user-images.githubusercontent.com/76586084/184856556-13467ccb-5476-4887-81d1-ca31bab5fa4f.png)

![image](https://user-images.githubusercontent.com/76586084/184856523-c25c6f7c-391d-4e26-af9f-ce8f2afd732b.png)

위에비해 굉장히 단순해 졌다.

errorCode는 기존에 내가 입력한 것과 다르다 예를들어 나는 에러 코드에 required.member.number 이렇게 넣었는데 여기서는 required만 넣어도 작동을 하는 것을 볼 수 있다.

이것은 축약된 errorCode인것을 알 수 있다. 이것을 ***messageCodeResolver*** 때문:exclamation:

##### messageCodeResolver - :fire:

test를 통해 알아보자

![image](https://user-images.githubusercontent.com/76586084/184873742-66414607-6b14-451a-ae39-15ba3ca0aef7.png)

![image](https://user-images.githubusercontent.com/76586084/184873799-18d92c7b-3219-41ab-8de4-f14885a34070.png)

보면 알 수 있듯이 resolveMessageCodes를 사용하면 에러코드가 여러개 자동으로 생성된다. 

***```BindingResult.rejectValue()```***를 사용하면 자동으로 저 codesResolver를 호출해 ***```New FieldError()```*** 여기에 저기 생성된 에러 코드들을 순서대로 넣어준다. 제일 Detail한 순서대로!!!
required.member.number << required.number << required.java.lang.String << required



##### ***```DefaultMessageCodesResolver```*** 의 기본 메시지 생성 규칙

##### 객체오류

```
객체 오류의 경우 다음 순서로 2가지 생성
1.: code + "." + object name
2.: code

예) 오류 코드 : required, object name : member
1.: required.member
2.: required
```

##### 필드오류

```
필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
1.: code + "." + object name + "." + field
2.: code + "." + field
3.: code + "." + field type
4.: code

예) 오류 코드 : typeMismatch, object name "user", field "age", field type : int
1.: "typeMismatch.user.age"
2.: "typeMismatch.age"
3.: "typeMismatch.int"
4.: "typeMismatch"
```

























