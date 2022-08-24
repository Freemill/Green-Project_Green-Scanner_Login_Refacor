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

```java
@PostMapping("/memberInsert")
public String memberInsertV1(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    log.info("userEmail = {}", member.getUserEmail());
    //검증 로직
    if (member.getNumber() == null) {
        bindingResult.addError(new FieldError("member", "number","번호 입력은 필수입니다."));
    }
    if (!StringUtils.hasText(member.getNickName())) {
        bindingResult.addError(new FieldError("member", "nickName", "닉네임 입력은 필수입니다."));
    }
    if (memberService.nickNameDuplicateCheck(member.getNickName())) {
        bindingResult.addError(new FieldError("member", "nickName", "중복된 닉네임입니다."));
    }
    if (!StringUtils.hasText(member.getPassword())) {
        bindingResult.addError(new FieldError("member", "password", "비밀번호 입력은 필수입니다."));
    }
    if (!StringUtils.hasText(member.getPasswordConfirm())) {
        bindingResult.addError(new FieldError("member", "passwordConfirm", "비밀번호 확인 입력은 필수입니다."));
    }
    if (!StringUtils.hasText(member.getUserEmail())) {
        bindingResult.addError(new FieldError("member", "userEmail", "이메일 입력은 필수입니다."));
    }
    if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
        bindingResult.addError(new FieldError("member", "userEmail", "이메일 형식에 맞춰 입력해주세요."));
    }
    if (!member.isPrivacyCheck()) {
        bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
    }
    if (!member.isTermsCheck()) {
        bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
    }
```

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



사용자의 입력 데이터가 컨트롤러의 ***```@ModelAttribute```*** 에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어렵다. 예를 들어서 숫자에 숫자가 아닌 문자가 입력된다면 가격은 ```Integer``` 타입으로 문자를 보관할 수 있는 방법이 없다. 그래서 오류가 발생한 경우 사용자가 입력 값을 보관하는 별도의 방법이 필요하다. 그리고 이렇게 보관한 사용자 입력 값을 검증 오류 발생시 화면에 다시 출력하면 된다.

```FieldError```는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공한다.



이제부터 본격적으로 오류 코드와 메시지에 대하여 알아보자. :fire::fire: :fireworks:

현재까지 

![image](https://user-images.githubusercontent.com/76586084/184830151-98918440-2ec2-435b-b4b9-acf8c491d6dc.png)

이런식으로 오류 메시지를 생성해 주었다. 하지만 이런 메시지도 한군데에서 모아놓고 관리해주면 상당히 관리하기에도 편하고 사용에도 편리할것 같다!

이것을 할 수 있는 기능이 킹프링에는 존재한다! properties 파일을 활용하면된다.

![image](https://user-images.githubusercontent.com/76586084/184831720-88f972ce-bbb8-4749-aa0d-eaaa993d1180.png)

***```codes```*** , ***```arguments```*** 를 활용하면 된다!  



![image-20220816175216070](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220816175216070.png)

errors.properties를 만들어줌! 

```applications.properties
spring.messages.basename = errors
```

추가해줌!

그리고

```errors.properties
required.member.number = 회원 숫자 입력은 필수입니다.
range.member.number = 숫자는 {0} ~ {1}까지 입력 가능합니다.
required.member.nickName = 회원 닉네임 입력은 필수입니다.
duplicated.member.nickName = 중복된 닉네임 입니다.
required.member.password = 비밀번호 입력은 필수입니다.
required.member.passwordConfirm = 비밀번호 확인 입력은 필수입니다.
required.member.userEmail = 이메일 입력은 필수입니다.
formCheck.member.userEmail = 이메일 입력 형식에 맞춰주세요.
passwordSame = 비밀번호와 비밀번호 확인이 일치하지 않습니다.
```

이렇게 오류 메시지를 추가해 준다.

또한 아래와 같이 오류메시지를 사용하게 Controller또한 Version upgrade를 한다.

```java
@PostMapping("/memberInsert")
public String memberInsertV3(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    log.info("userEmail = {}", member.getUserEmail());
    //검증 로직
    if (member.getNumber() == null) {
        bindingResult.addError(new FieldError("member", "number", member.getNickName(), false, new String[]{"required.member.number"}, null, null));
    }
    if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
        bindingResult.addError(new FieldError("member", "number", member.getNumber(), false, new String[]{"range.member.number"}, new Object[]{0, 1000}, null));
    }
    if (!StringUtils.hasText(member.getNickName())) {
        bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, new String[]{"required.member.nickName"}, null, null));
    }
    if (memberService.nickNameDuplicateCheck(member.getNickName())) {
        bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, new String[]{"duplicated.member.nickName"}, null, null));
    }
    if (!StringUtils.hasText(member.getPassword())) {
        bindingResult.addError(new FieldError("member", "password", member.getPassword(), false, new String[]{"required.member.password"}, null,  null));
    }
    if (!StringUtils.hasText(member.getPasswordConfirm())) {
        bindingResult.addError(new FieldError("member", "passwordConfirm", member.getPasswordConfirm(), false, new String[]{"required.member.passwordConfirm"}, null, null));
    }
    if (!StringUtils.hasText(member.getUserEmail())) {
        bindingResult.addError(new FieldError("member", "userEmail", member.getUserEmail(), false, new String[]{"required.member.userEmail"}, null, null));
    }
    if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
        bindingResult.addError(new FieldError("member", "userEmail", member.getUserEmail(), false, new String[]{"formCheck.member.userEmail"}, null, null));
    }
    if (!member.isPrivacyCheck()) {
        bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
    }
    if (!member.isTermsCheck()) {
        bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
    }
    
    // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
    if (member.getPassword() != null && member.getPasswordConfirm() != null) {
        String memberPassword = member.getPassword();
        String memberPasswordConfirm = member.getPasswordConfirm();
        if (memberPassword != memberPasswordConfirm) {
            bindingResult.addError(new ObjectError("member", new String[]{"passwordSame"}, null, "비밀번호와 비밀번호확인의 입력값을 같은 값이어야 합니다."));
            }
        }
```

![image-20220816185527175](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220816185527175.png)

잘 작동하는 것을 확인할 수 있다.

```java
//codes에 아래와 같이 String[]{}같이 넣어주는 이유는 default를 이용하거나 오류메시지가 없을 때를 대비해 여러개의 오류메시지를 넣을 수 있기 때문이다.
bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, new String[]{"duplicated.member.nickName"}, null, null));
```



여기서 또 Version up을 해보자 .

지금까지 ***```FieldError```*** 와 ***```ObjectError```*** 를 다루기 너무 번거로웠다.
또한 오류코드도 조금 더 자동화 시킬 수 있을것 같다.



컨트롤러에서 ***```BindingResult```*** 는 검증해야 할 객체인 ***```target```*** 바로 다음에 온다. 따라서  ***```BindingResult```*** 는 이미 본인이 검증해야 할 객체인 ***```target```*** 을 알고 있다.



##### ***```rejectValue()```*** , ***```reject()```***  

그리고 ***```BindingResult```*** 가 제공하는 ***```rejectValue()```*** , ***```reject()```***  를 사용하면 ***```FieldError```*** , ***```ObjectError```*** 를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.

***```rejectValue()```*** , ***```reject()```***  를 사용해서 기존 코드를 단순화 해보자. :shallow_pan_of_food:

```java
@PostMapping("/memberInsert")
    public String memberInsertV4(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        log.info("userEmail = {}", member.getUserEmail());
        //검증 로직
        if (member.getNumber() == null) {
            bindingResult.rejectValue("number", "required");
        }
        if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
            bindingResult.rejectValue("number", "range", new Object[]{0, 1000}, null);
        }
        if (!StringUtils.hasText(member.getNickName())) {
            bindingResult.rejectValue("nickName", "required");
        }
        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
            bindingResult.rejectValue("nickName", "duplicated");
        }
        if (!StringUtils.hasText(member.getPassword())) {
            bindingResult.rejectValue("password", "required");
        }
        if (!StringUtils.hasText(member.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "required");
        }
        if (!StringUtils.hasText(member.getUserEmail())) {
            bindingResult.rejectValue("userEmail", "required");
        }
        if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
            bindingResult.rejectValue("userEmail", "formCheck");
        }
        if (!member.isPrivacyCheck()) {
            bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
        }
        if (!member.isTermsCheck()) {
            bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
        }
```

```java
// password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
if (member.getPassword() != null && member.getPasswordConfirm() != null) {
    String memberPassword = member.getPassword();
    String memberPasswordConfirm = member.getPasswordConfirm();
    if (!memberPassword.equals(memberPasswordConfirm)) {
        bindingResult.reject("passwordSame");
    }
}
```

위에비해 굉장히 단순해 졌다.

errorCode는 기존에 내가 입력한 것과 다르다 예를들어 나는 에러 코드에 required.member.number 이렇게 넣었는데 여기서는 required만 넣어도 작동을 하는 것을 볼 수 있다.

이것은 축약된 errorCode인것을 알 수 있다. 이것을 ***messageCodeResolver*** 때문:exclamation:

##### messageCodeResolver - :fire:

test를 통해 알아보자

```java
public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    @DisplayName("Obejct Error Test")
    void messageCodesResolverObject(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "member");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        System.out.println("----------------------");
        Assertions.assertThat(messageCodes).containsExactly("required.member", "required");
    }

    @Test
    void messageCodesResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "member", "number" ,String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }

    }

}
```

![image](https://user-images.githubusercontent.com/76586084/184873799-18d92c7b-3219-41ab-8de4-f14885a34070.png)

보면 알 수 있듯이 resolveMessageCodes를 사용하면 에러코드가 여러개 자동으로 생성된다. 

***```BindingResult.rejectValue()```*** 를 사용하면 자동으로 저 codesResolver를 호출해 ***```New FieldError()```***  여기에 저기 생성된 에러 코드들을 순서대로 넣어준다. 제일 Detail한 순서대로!!!
required.member.number << required.number << required.java.lang.String << required



##### ***```DefaultMessageCodesResolver```***  의 기본 메시지 생성 규칙

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





## Day 6

---

##### 오류 코드 관리 전략

***핵심은 구체적인 것에서! 덜 구체적인 것으로!***

***```MessageCodesResolver```*** 는 ***```required.member.name```*** 처럼 구체적인 것을 먼저 만들어주고, ***```requied```*** 처럼 덜 구체적인 것을 나중에 만든다.



***왜 이렇게 복잡하게 사용하는가?***

모든 오류 코드에 대해서 메시지를 각각 다 정의하면 개발자 입장에서 관리하기 힘들다.
크게 중요하지 않은 메시지는 범용성 있는 ***```requied```***  같은 메시지로 끝내고, 정말 중요한 메시지는 꼭 필요할 때 구체적으로 적어서 사용하는 방식이 더 효과적이다.

아래와 같이 만들어보자!:rotating_light:

```
#==FieldError==
#Level1
required.member.number = 회원 숫자 입력은 필수입니다.
range.member.number = number는 {0} ~ {1}까지 입력 가능합니다.
required.member.nickName = 회원 닉네임 입력은 필수입니다.
duplicated.member.nickName = 중복된 닉네임 입니다.
required.member.password = 비밀번호 입력은 필수입니다.
required.member.passwordConfirm = 비밀번호 확인 입력은 필수입니다.
required.member.userEmail = 이메일 입력은 필수입니다.
formCheck.member.userEmail = 이메일 입력 형식에 맞춰주세요.


#Level2 - 생략

#Level3
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
range.java.lang.String = 문자는 {0} ~ {1}까지 입력 가능합니다.
range.java.lang.Integer = 숫자는 {0} ~ {1}까지 입력 가능합니다.
duplicated.java.lang.String = 중복된 문자입니다.
formCheck.java.lang.String = 이메일 입력 형식에 맞춰주세요.


#Level4
required = 필수 값 입니다.
range = {0} ~ {1}까지 범위를 허용합니다..
formCheck = 틀린 형식 입니다.
duplicated = 중복된 문자입니다.
```

Level1은 굉장히 Detail하게 갈수록 Rough하게



:e-mail:참고

```java
if (!StringUtils.hasText(member.getName())){
    bindingResult.rejectValue("name", "required");
}

// 위와 아래는 같은 기능을 한다.

ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "name", "required")
```





이제 아기다리고기다리던! 타입 오류를 핸들링해보자!

![image](https://user-images.githubusercontent.com/76586084/185107687-7c136350-8e3a-440f-9faf-f9154b60c7ab.png)

현재는 이런 심란한 오류 메시지가 사용자에게 그대로 노출되고 있다. 보안에도 문제지만 야생의 사용자는 홈페이지 관리 상태를 의심할 것이다!



검증 오류는 다음과 같이 2가지로 구분 할 수 있다.

- 개발자가 직접 설정한 오류 코드 -> ***```rejectValue()```*** 를 직접 호출
- 스프링이 직접 검증 오류에 추가한 경우(주로 타입 정보가 맞지 않음)





지금까지 학습한 메시지 코드 전략의 강점을 지금부터 실감해보자 :fearful:

![image](https://user-images.githubusercontent.com/76586084/185107687-7c136350-8e3a-440f-9faf-f9154b60c7ab.png)

사실 이것만 보고도 알 수 있다. Integer 형임에도 String이 Controller 안으로 넘어 온 것이다! 또한 

![image-20220817203353473](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220817203353473.png)

이렇게  TypeMismatch라고 errorCode도 생성해 주었다! 이것은 스프링이 직접 해준것!  :+1:

4가지 errorCode를 보면 다음과 같다.

- ***```typeMistmatch.memer.number```*** 
- ***```typeMistmatch.number```*** 
- ***```typeMistmatch.java.lang.Integer```*** 
- ***```typeMistmatch```*** 

위에서 봤던것과 똑같은 형태!

내가 저것을 ***errors.properties***에 입력 안해놓아서 defaultMessage가 나온것! 내가 직접 담으면!

```errors.properties
#추가 
typeMismatch.java.lang.Integer = 숫자를 입력해주세요.
typeMismatch = 타입 오류입니다.
```

이렇게 해주면!

![image](https://user-images.githubusercontent.com/76586084/185109658-f20f64c3-eba2-4c7d-8efd-8fc774525d7f.png)

짠!:golfing_man: 역시 Kingpring이다. 아주 잘 나오는 것을 확인할 수 있다.:golf:

아래에 회원숫자 입력은 필수입니다.의 메시지는 type오류시 Model을 생성 못하고 그렇기 때문에 null로 들어오면서 생기는 메시지이다. 추가 조건을 넣어서 빼줄 수 있다.

예를들면 아래의 코드를 첫줄에 넣어서 해결 가능

```java
if (bindingResult.hasErrors()){
    log.info("errors = {}", bindingREsult);
    return "validation/v2/addForm"
}
```





## Day 7

---

```java
@PostMapping("/memberInsert")
public String memberInsertV4(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    log.info("userEmail = {}", member.getUserEmail());
    //검증 로직
    if (member.getNumber() == null) {
        bindingResult.rejectValue("number", "required");
    }
    if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
        bindingResult.rejectValue("number", "range", new Object[]{0, 1000}, null);
    }
    if (!StringUtils.hasText(member.getNickName())) {
        bindingResult.rejectValue("nickName", "required");
    }
    if (memberService.nickNameDuplicateCheck(member.getNickName())) {
        bindingResult.rejectValue("nickName", "duplicated");
    }
    if (!StringUtils.hasText(member.getPassword())) {
        bindingResult.rejectValue("password", "required");
    }
    if (!StringUtils.hasText(member.getPasswordConfirm())) {
        bindingResult.rejectValue("passwordConfirm", "required");
    }
    if (!StringUtils.hasText(member.getUserEmail())) {
        bindingResult.rejectValue("userEmail", "required");
    }
    if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
        bindingResult.rejectValue("userEmail", "formCheck");
    }
    if (!member.isPrivacyCheck()) {
        bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
    }
    if (!member.isTermsCheck()) {
        bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
    }

    // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
    if (member.getPassword() != null && member.getPasswordConfirm() != null) {
        String memberPassword = member.getPassword();
        String memberPasswordConfirm = member.getPasswordConfirm();
        if (memberPassword != memberPasswordConfirm) {
            bindingResult.reject("passwordSame");
        }
    }

    //검증에 실패하면 다시 입력 폼으로!
    if (bindingResult.hasErrors()) {
        log.info("errors = {}", bindingResult);
        return "html/memberInsertForm";
    }

    // 성공 로직
    memberService.join(member);

    return "redirect:/";
}
```

전체 코드를 보면 느끼겠지만, 성공로직은 굉장히 짧은데 비해 검증 로직은 상당히 길다. 검증 로직을 별도로 모아 관리하는 Validator라는 class를 만들고 검증 코드를 따로 빼자! :whale: (코드가 훨씬 깔끔해지고 유지보수하기도 쉬워짐)

![image](https://user-images.githubusercontent.com/76586084/185343258-2440f0b3-cfa3-4aaa-a8a5-f384f50d6d56.png)

memberValidator class 생성

memberValidator에 검증 로직들을 옮기고 기존 검증 로직을 삭제한다.

```java
package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MemberValidator implements Validator {

    private final MemberServiceImpl memberService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Member.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Member member = (Member) target;


        //검증 로직
        if (member.getNumber() == null) {
            errors.rejectValue("number", "required");
        }
        if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
            errors.rejectValue("number", "range", new Object[]{0, 1000}, null);
        }
        if (!StringUtils.hasText(member.getNickName())) {
            errors.rejectValue("nickName", "required");
        }
        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
            errors.rejectValue("nickName", "duplicated");
        }
        if (!StringUtils.hasText(member.getPassword())) {
            errors.rejectValue("password", "required");
        }
        if (!StringUtils.hasText(member.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "required");
        }
        if (!StringUtils.hasText(member.getUserEmail())) {
            errors.rejectValue("userEmail", "required");
        }
        if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
            errors.rejectValue("userEmail", "formCheck");
        }
        if (!member.isPrivacyCheck()) {
            errors.rejectValue("privacyCheck", "required");
        }
        if (!member.isTermsCheck()) {
            errors.rejectValue("termsCheck", "required");
        }

        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
            String memberPassword = member.getPassword();
            String memberPasswordConfirm = member.getPasswordConfirm();
            if (memberPassword != memberPasswordConfirm) {
                errors.reject("passwordSame");
            }
        }



    }
    private boolean emailTypeCheck(String userEmail) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEmail);
        return !matcher.matches();
    }
}
```

##### @Component를 넣어줌



그리고 기존의 코드와 아래와 같이 수정해준다.

```java
@Controller
@RequiredArgsConstructor
@Slf4j
public class ValidationMemberControllerV2 {

    private final MemberServiceImpl memberService;
    private final MemberValidator memberValidator;

    @GetMapping("/memberInsertForm")
    public String memberInsert(Model model) {
        log.info("memberInsertForm IN");
        model.addAttribute("member", new Member());
        return "html/memberInsertForm";
    }

    @PostMapping("/memberInsert")
    public String memberInsertV5(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        memberValidator.validate(member, bindingResult);

        //검증에 실패하면 다시 입력 폼으로!
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "html/memberInsertForm";
        }

        // 성공 로직
        memberService.join(member);

        return "redirect:/";
    }
```



![image](https://user-images.githubusercontent.com/76586084/185340060-e1b7f1a1-5a9e-4bdf-ad7e-f109e5aa7e2a.png)

잘 동작하는것을 확인할 수 있다.





스프링이 ***```Validator```***  인터페이스르르 별도로 제공하는 이뉴느 체계적으로 검증 기능을 도입하기 위해서다. 그런데 앞에서는 검증기를 직접 불러서 사용했고, 이렇게 사용해도 된다. 그런데 ***```Validator```***  인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.



##### WebDataBinder를 통해서 사용해보기

***```WebDataBinder```*** 는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함된다.



***ValidationItemControllerV2***에 다음 코드를 추가하자

```java
@InitBinder
public void init(WebDataBinder dataBinder){
    log.info("init binder {}", dataBinder);
    dataBinder.addValidators(memberValidators);
}
```

이렇게 해 놓으면 이제 컨트롤러에 들어올 때 마다 항상 위의 로직이 불려져서 항상 WebDataBinder가 새로 만들어지고 그게 저 로직에 들어오면서 항상 memberValidators의 검증기를 적용하게 됨

그리고 ***memberInsertV6*** 아래와 같이 수정

```java
@PostMapping("/memberInsert")
public String memberInsertV5(@Validated @ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

    //검증에 실패하면 다시 입력 폼으로!
    if (bindingResult.hasErrors()) {
        log.info("errors = {}", bindingResult);
        return "html/memberInsertForm";
    }

    // 성공 로직
    memberService.join(member);

    return "redirect:/";
}
```

이렇게 하면 이제***```@Validated```*** 가 붙어있는 것에서는 ***```@InitBinder```*** 가 동작한다.



***동작 방식***

***```@Validated```*** 는 검증기를 실행하라는 애노테이션이다.
이 애노테이션이 붙으면 앞서 ***```WebDataBinder```*** 에 등록한 검증기를 찾아서 실행한다. 그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다. 이때 ***```supports()```*** 가 사용된다. 여기서 ***```supports(Member.class)```*** 가 호출되고, 결과가 ***true***이므로 ***```ItemValidator```*** 의 ***```validate()```***  가 호출된다.



##### 글로벌 설정 - 모든 컨틀롤러에 다 적용

```java
@SpringBootApplication
public class GbcollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(GbcollectorApplication.class, args);
	}
    
    @Override
    public Validator getValidator(){
        return new MemberValidation();
    }

}
```

이렇게 넣으면 됨 이렇게 하면 기존 컨트롤러의 ***```@InitBinder```*** 를 제거해도 글로벌 설정으로 정상 동작하는 것을 확인할 수 있다.



##### 참고

> 검증시 ***```@Validated```***  , ***```@Valid```***  둘 다 사용 가능하다.
> ***```javax.validation.@Valid```*** 를 사용하려면 ***```build.gradle```***  의존 관계가 추가가 필요
> ***```implementation 'org.springframework.boot::spring-boot-starter-validation```*** 
> ***```@Validated```***  는 스프링 전용 검증 애노테이션이고, ***```@Valid```*** 는 자바 표준 검증 애노테이션이다.
> 자세한 내용은 다음에!





### Bean Validation - 소개

검증 기능을 지금처럼 매번 코드로 작성하는 것은 상당히 번거롭다. 특히 특정 필드에 대한 검증 로직은 대부분 빈 값인지 아닌지, 특정 크기를 넘는지 아닌지와 같이 매우 일반적인 로직이다. 다음 코드를 보자.

ex) ***```@NotBlank```***  , ***```@NotNull```*** .  ***```Range(Min=, max=)```*** 

이런 검증 로직을 모든 프로젝트에 적용하 수 있게 고통화하고, 표준화 한 것이 바로 ***Bean Validation***이다.
***Bean Validation***을 잘 활용하면, 애노테이션 하나로 검증 로직을 매우 편리하게 적용할 수 있다.



***```BeanValidation```*** 이란?

먼저 Bean Validation은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380)이라는 기술 표준이다. 쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다. 마치 JPA가 표준 기술이고 그 구현체로 하이버네이트가 있는 것과 같다.







### Bean Validation - 시작

***```Bean Validation``` 의존 관계 추가***

```build.gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

![image](https://user-images.githubusercontent.com/76586084/185367271-7cc071e1-3e61-4713-8446-ea10ca39499a.png)

실제로 들어가 있다. 얘들은 인터페이스 실제 동작은 hibernate에서



***```Member```*** 

```java
package com.garb.gbcollector.login.domain.membervo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Member {

    private Long id;

    @NotNull
    @Range(min=1, max=1000)
    private Integer number;

    @Email
    private String userEmail;

    @NotBlank
    private String nickName;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirm;

    @AssertTrue
    private boolean privacyCheck;

    @AssertTrue
    private boolean termsCheck;

    public Member(){}

    public Member(Integer number, String userEmail, String nickName, String password, String passwordConfirm, boolean privacyCheck, boolean termsCheck) {
        this.number = number;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.privacyCheck = privacyCheck;
        this.termsCheck = termsCheck;
    }
}
```

Annotation을 추가해 주었다.

Annotation에 대한 목록은 https://hyeran-story.tistory.com/81 참조



##### Unit Test를 진행해보자!

```java
package com.garb.gbcollector.domain.validation;

import com.garb.gbcollector.login.domain.membervo.Member;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest {

    @Test
    void beanValidation(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Member member = new Member();
        member.setNumber(0);
        member.setUserEmail("sfsadf");
        member.setNickName(" ");
        member.setPassword(" ");
        member.setPasswordConfirm("as");
        member.setPrivacyCheck(false);
        member.setTermsCheck(false);

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        for (ConstraintViolation<Member> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }

    }
```

***>>>*** 실행결과

![image](https://user-images.githubusercontent.com/76586084/185377145-53e1e638-523d-46c4-b1bf-bee0d232fd63.png)

메시지를 넣어준 적이 없어서 기본적으로 설정되어있는 메시지들이 나오고 있지만 작동이 잘 되는것을 확인할 수 있다.





본격적으로 ***```Bean Validation```*** 을 적용해보자! :gear::gear:

##### ValidationItemControllerV3 를 만들자!

```java
package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ValidationMemberControllerV3 {

    private final MemberServiceImpl memberService;

    @GetMapping("/memberInsertForm")
    public String memberInsert(Model model) {
        log.info("memberInsertForm IN");
        model.addAttribute("member", new Member());
        return "html/memberInsertForm";
    }

    @PostMapping("/memberInsert")
    public String memberInsert(@Validated @ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        //검증에 실패하면 다시 입력 폼으로!
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "html/memberInsertForm";
        }

        // 성공 로직
        memberService.join(member);

        return "redirect:/";
    }

}
```

***```MemberValidator```***  도 빠지고 없다 하지만! :scream:

![image](https://user-images.githubusercontent.com/76586084/185380061-fc1dd202-2601-4bc4-a32f-e3506702f894.png)

제대로 작동을 한다! ***```Member```*** 가 적용이 됀 모습니다!

어떻게 자동으로 적용을 해서 사용을 했을까? 
아까*** ```implementation 'org.springframework.boot:spring-boot-starter-validation'```***  이것을 추가한 것이 그 의문의 답이다!:speak_no_evil:

저 library가 있으면 Spring이 실행될 때 자동으로 ***```LocalValidatorFactoryBean```*** 을 글로벌 ***```Validator```*** 로 등록한다. 
이***```Validator```*** 는 ***```NotNull```*** 같은 애노테이션을 보고 검증을 수행한다. 이렇게 ***```Validator```*** 가 적용되어 있기 때문에, ***```@Valid```*** . ***```@Validaed```*** 만 적용하면 된다. 검증 오류가 발생하면, ***```FieldError```*** , ***```ObjectError```*** 를 생생ㅅ해서 ***```BindingResult```*** 에 담아준다.



***```@Validated```*** , ***```@Valid```***  둘 중 하나를 사용해도 모두 작동한다. Valid는 자바 표준 검증 애노테이션이고 Validated는 스프링 전용 검증 애노테이션이다 둘다 기능은 거의 동일하지만 ***````Validated```*** 는 ***```groups```*** 라는 기능을 제공한다.





### 검증순서

1. ***```@ModelAttribute```***  각각의 필드에 타입 변환 시도
   1. 성공하면 다음으로
   2. 실패하면 ***```typeMismatch```*** 로 ***```FieldError```*** 추가
2. ***```Validator```*** 적용



***Binding에 성공한 필드만 Bean Validation 적용***

BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.
(일단 모델 객체에 바인딩 받는 값이 정상적으로 들어와야 검증도 의미가 있다.)

***```@ModelAttribute```***  -> 각각의 필드 타입 변환 시도 -> 변환에 성공한 필드만 BeanValidation 적용

***예)***

- ***```nickName```*** 에 문자 "A"입력 -> 타입 변환 성공 -> ***```itemName```***  필드에 BeanValidation 적용
- ***```number```*** 에 문자 "A" 입력 -> "A"를 숫자 타입 변환 시도 실패 -> typeMimatch FieldError 추가 -> ***```price```*** 필드는 BeanValidation 적용 X



##### Bean Validation - 에러 코드

Bean Validation이 기본으로 제공하는 오류 메시지를 좀 더 자세히 변경하고 싶으면 어떻게 하면 될까?

```
Field error in object 'member' on field 'passwordConfirm': rejected value []; codes [NotBlank.member.passwordConfirm,NotBlank.passwordConfirm,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.passwordConfirm,passwordConfirm]; arguments []; default message [passwordConfirm]]; default message [공백일 수 없습니다]
Field error in object 'member' on field 'password': rejected value []; codes [NotBlank.member.password,NotBlank.password,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.password,password]; arguments []; default message [password]]; default message [공백일 수 없습니다]
Field error in object 'member' on field 'userEmail': rejected value []; codes [NotBlank.member.userEmail,NotBlank.userEmail,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.userEmail,userEmail]; arguments []; default message [userEmail]]; default message [공백일 수 없습니다]
Field error in object 'member' on field 'termsCheck': rejected value [false]; codes [AssertTrue.member.termsCheck,AssertTrue.termsCheck,AssertTrue.boolean,AssertTrue]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.termsCheck,termsCheck]; arguments []; default message [termsCheck]]; default message [true여야 합니다]
Field error in object 'member' on field 'number': rejected value [null]; codes [NotNull.member.number,NotNull.number,NotNull.java.lang.Integer,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.number,number]; arguments []; default message [number]]; default message [널이어서는 안됩니다]
Field error in object 'member' on field 'privacyCheck': rejected value [false]; codes [AssertTrue.member.privacyCheck,AssertTrue.privacyCheck,AssertTrue.boolean,AssertTrue]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [member.privacyCheck,privacyCheck]; arguments []; default message [privacyCheck]]; default message [true여야 합니다]
```

에러코드가 이전에 공부했던 것처럼 자동으로 넣어져 있다!! 우리는 저것을 수정하면 됨!

##### ex)

***```@NotBlank```*** 

- NotBlank.member.number
- NotBlank.number
- NotBlank.java.lang.String
- NotBlank



***```@Range```*** 

- Range.member.number
- Range.number
- Range.java.lang.Integer
- Range



직접 해보자! errors.properties에 아래 추가

```
NotBlank = 공백X
Range = {0}, {2} ~ {1} 허용
```

![image](https://user-images.githubusercontent.com/76586084/185390390-3665c8b6-46d9-4215-8dfa-7cabbdda65db.png)

제대로 동작하는 것을 확인할 수 있다.

예)

```java
@Data
public class Member {

    private Long id;

    @NotNull
    @Range(min=1, max=1000)
    private Integer number;

    @NotNull
    @Email(message="이메일 형식에 맞춰 주세요")
    private String userEmail;

    @NotBlank
    private String nickName;

    @NotBlank
    private String password;
```

위와같이 errorMessage를 설정할 수도 있다.





***BeanValidation 메시지 찾는 순서***

1. 생성된 메시지 코드 순서대로 ***```messageSource```*** 에서 메시지 찾기
2. 애노테이션의 ***```message```***  속성 사용 -> ***```@NotBlank(message="공백!" {0})```*** 
3. 라이브러리 제공하는 기본 값 사용 -> 공백일 수 없다.





### Bean Validation - 오브젝트 오류

Bean Validation에서 특정 필드***```('FieldError')```*** 가 아닌 해당 오브젝트 관련 오류 ***```('ObjectError')```*** 는 어떻게 처리할 수 있을까?

***```@ScriptAssert()```*** 라는 기능이 있지만, 사용하기 복잡하고 제약이 너무 많다.

따라서 ***```ObjectError```*** 나 조금 복잡한 ***```FieldError```*** 는 직접 bindingResult로 처리해주는것이 맞다.

```java
    @PostMapping("/memberInsert")
    public String memberInsert(@Validated @ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
            bindingResult.rejectValue("nickName", "duplicated");
        }

        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
            String memberPassword = member.getPassword();
            String memberPasswordConfirm = member.getPasswordConfirm();
            if (memberPassword != memberPasswordConfirm) {
                bindingResult.reject("passwordSame");
            }
        }


        //검증에 실패하면 다시 입력 폼으로!
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "html/memberInsertForm";
        }

        // 성공 로직
        memberService.join(member);

        return "redirect:/";
    }

}
```







## Day 8

---

#### Baen Validation -한계

만약 내가 Member class를 제약 조건이 다른 다른 로직에서 사용하려고 한다면 제약 조건간에 일치하지 않는 부분이 생겨 유기적으로 사용할 수 없게 된다. 그러한 문제를 해결해 보고자 ***```groups```***  기능이 있다.



#### Bean Validation - groups

동일한 모델 객체를 동록할 때와 수정할 때 각각 다르게 검증하는 방법 (실무에서는 잘 사용하지 않는다. 복잡도가 올라감)





#### Form 전송 객체 분리

실무에서는 ***```groups```*** 를 잘 사용하지 않는다. 그 이유는 등록시 폼에서 전달하는 데이터가 ***```Item```***  도메인 객체와 딱 맞지 않기 때문이다.

#### 

   

#### Bean Validation - HTTP 메시지 컨버터

***```@Valid```*** , ***```@Validated```*** 는  ***```HttpMessageConverter```***  ***```(@RequestBody)```***  에도 적용할 수 있다.



>***``` 참고 ```*** 
>
>***```@ModelAttribute```***  는 Http 요청 파라미터(URL 쿼리 스트링, POST Form)를 다룰 때 사용한다.
>***```@RequestBody```*** 는 Http Body의 데이터를 객체로 변환할 때 사용한다. 주로 API JSON 요청을 다룰 때 사용한다.





API로 정보를 보내는 실습을 해보자 Controller의 코드는 아래와 같다.

##### ValidationMemberApiController

```java
package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/members")
public class ValidationMemberApiController {

    @PostMapping("/add")
    public Object addMember(@RequestBody @Validated MemberSaveForm form, BindingResult bindingResult) {
        log.info("API 컨틀롤러 호출");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors = {}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");

        return form;
    }
}
```



이 로직을 확인해보기 위해 ***```PostMan```*** 을 사용한다.

![image](https://user-images.githubusercontent.com/76586084/185622292-f88a33be-9353-409a-853d-3614fd0c99ed.png)

​	

PostMan을 이용해 API를 JSON 형태롤 호출했다.! :happy:

![image](https://user-images.githubusercontent.com/76586084/185622417-0f9e24e1-f27a-4ef5-98a5-ca76fd59948f.png)



Log는 성공적으로 호출 되었고!

![image](https://user-images.githubusercontent.com/76586084/185622545-bcd6d38e-285a-45e8-bcde-e8e84d2577e8.png)

성공적으로 ReponseBody도 작동하였다!



실패 상황도 보자 :fearful:

![image](https://user-images.githubusercontent.com/76586084/185622857-0962040a-6074-4370-b478-1ef76e55faa7.png)

userEmail을 형식에 맞게 보내지 않았다!

![image](https://user-images.githubusercontent.com/76586084/185623085-4d74a70c-faa2-4c33-b3fc-1c5e595a7ea0.png)

당연히 오류가 발생했고, Controller 호출도 되었다 :cowboy_hat_face:

![image](https://user-images.githubusercontent.com/76586084/185623231-3046eb09-017e-4b79-8b4f-ffd735c56a6e.png)

***```@ResponseBody```*** 에 오류메시지가 잘 나왔다



이번엔 ***```TypeError```*** 를 확인해보자

![image](https://user-images.githubusercontent.com/76586084/185623631-edace59a-2ab1-4922-87ce-3c2309c5c896.png)

number에 String Type을 보냈다. :disappointed:

```
2022-08-19 21:59:20.774  WARN 2328 --- [nio-8080-exec-9] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `java.lang.Integer` from String "sdf": not a valid `java.lang.Integer` value; nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.lang.Integer` from String "sdf": not a valid `java.lang.Integer` value<EOL> at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 2, column: 16] (through reference chain: com.garb.gbcollector.login.domain.membervo.MemberSaveForm["number"])]

```

이런 오류 메시지만 뜨고 Controller는 작동하지 않았다...

![image](https://user-images.githubusercontent.com/76586084/185623861-d82dccb0-406b-4068-87f5-8759b2ac88c9.png)

이렇게 뜸.



#### :last_quarter_moon_with_face: 어쨋든 JSON 객체로 ***```MemberSaveForm```*** 를 만들어야 하는데 못 만듦:first_quarter_moon_with_face:

기억해두자(후에 이를 해결할 것이다....)



***```@ModelAttribute```*** ***vs*** ***```@RequestBody```*** 

Http 요청 파라미터를 처리하는 ***```@ModelAttribute```*** 는 각각의 필드 단위로 세밀하게 적용된다. 그래서 특정 필드에 타입이 맞지 않는 오류가 발생해도 나머지 필드는 정상 처리할 수 있다. 

***```HttpMessageConverter```*** 는  ***```@ModelAttribute```*** 와 다르게 각각의 필드 단위로 적용되는 것이 아니라, 전체 객체 단위로 적용된다.
따라서 메시지 컨버터의 작동이 성공해서 ***```Member```*** 객체를 만들어야 ***```@Valid```*** , ***```@Validated```*** 가 적용된다.

- ***```@ModelAttribute```*** 는 필드 단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필드는 정상 바인딩 되고, Validator를 사용한 검증도 적용할 수 있다.
- ***```@RequestBody```***는 ***```HttpMessageConverter```*** 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자체가 진행않고 예외가 발생한다. 컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.



## Day 9

#### 로그인 기능 구현

초기 코드

***```LoginController```***

```java
package com.garb.gbcollector.login.domain.login;

import com.garb.gbcollector.login.web.login.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "html/login";
    }
}
```



***```LoginForm```***

```java
package com.garb.gbcollector.login.web.login;

import javax.validation.constraints.NotEmpty;

public class LoginForm {

    @NotEmpty
    private String userEmail;

    @NotEmpty
    private String password;
}
```



***```LoginTestDataInit```*** - 로그인 기능을 Test하기 위해 등록해놓은 빈

```java
package com.garb.gbcollector.login.web;

import com.garb.gbcollector.login.domain.memberdao.MemberRepository;
import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class LoginTestDataInit {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init(){
        memberRepository.save(new MemberSaveForm(1, "gildong@naver.com", "gildong", "killdong", "killdong", true, true));
        memberRepository.save(new MemberSaveForm(2, "chulsoo@naver.com", "chulsoo", "chulsuck", "chulsuck", true, true));
    }
}
```

:new: 스프링부트를 시작하자마자 2명의 회원을 등록해 놓는다



***```Login.html```***

![image](https://user-images.githubusercontent.com/76586084/185789471-35ab7764-e32a-4d2b-9d4c-f5c08564545c.png)

#### Form 전송 객체 분리 - 폼 데이터 전달을 위한 별도의 객체 사용(groups가 아닌 다른 방법을 사용해본다.)

위에서 로그인을 할 때 회원가입(number, userEmail, nickName, ...)을 할때와는 다른 데이터(userEmail, password)를 전송해야 하는것을 볼 수 있다. 따라서 전송 객체를 분리해서 사용해야 한다.

- ***```Html Form```*** -> ***```MemberLoginForm```*** -> ***```Controller```*** -> ***```Item 생성```*** -> ***```Repository```***
  - 장점 : 전송하는 폼 데이터가 복잡해도 거기게 맞춘 별도의 폼 객체를 사용해서 데이터를 전달 받을 수 있다. 보통 등록과, 수정용으로 별도의 폼 객체를 만들기 때문에 검증이 중복되지 않는다.
  - 단점 : 폼 데이터를 기반으로 컨토를로에서 Item 객체를 생성하는 변환 과정이 추가된다.





## Day 10

---

##### From 전송 객체를 분리해보자

![image-20220822193912298](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220822193912298.png)

domain에서 사용할 ***```Member```***  객체와 web영역에서 사용할 ***```MemberLoginForm```*** , ***```MemberSaveForm```*** 으로 분리한다.

***```Member```***

```java
package com.garb.gbcollector.login.domain.membervo;

import lombok.Data;

@Data
public class Member {

    private Long id;

    private Integer number;

    private String userEmail;

    private String nickName;

    private String password;

    private String passwordConfirm;

    private boolean privacyCheck;

    private boolean termsCheck;

    public Member(Integer number, String userEmail, String nickName, String password, String passwordConfirm, boolean privacyCheck, boolean termsCheck) {
        this.number = number;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.privacyCheck = privacyCheck;
        this.termsCheck = termsCheck;
    }
}
```

***```MemberSaveForm```***

```java
package com.garb.gbcollector.login.web.validation.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MemberSaveForm {

    private Long id;

    @NotNull
    @Range(min=1, max=1000)
    private Integer number;

    @NotBlank
    @Email
    private String userEmail;

    @NotBlank
    private String nickName;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirm;

    @AssertTrue
    private boolean privacyCheck;

    @AssertTrue
    private boolean termsCheck;

    public MemberSaveForm(){}

    public MemberSaveForm(Integer number, String userEmail, String nickName, String password, String passwordConfirm, boolean privacyCheck, boolean termsCheck) {
        this.number = number;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.privacyCheck = privacyCheck;
        this.termsCheck = termsCheck;
    }
}
```

***```MemberLoginForm```***

```java
package com.garb.gbcollector.login.web.validation.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MemberLoginForm {

    @NotEmpty
    private String userEmail;

    @NotEmpty
    private String password;

}
```

***```MemoryMemberRepository```***

```java
package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.validation.form.MemberLoginForm;
import com.garb.gbcollector.login.web.validation.form.MemberSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> memberStore = new ConcurrentHashMap<>();
    private static Long sequence = 0L;

    @Override
    public void save(Member member) {
        member.setId(++sequence);
        log.info("member = {}", member);
        memberStore.put(member.getId(), member);
    }

    @Override
    public Optional<Member> findByEmail(Member member) {
        return findAll().stream()
                .filter(m -> m.getUserEmail().equals(member.getUserEmail()))
                .findFirst();
    }

    @Override
    public boolean findByNickName(String nickName) {
        return !findAll().stream()
                .filter(m -> m.getNickName().equals(nickName))
                .findFirst().isEmpty();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(memberStore.values());
    }
}
```

***```ValidationMemberControllerV3```***

```java
package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.validation.form.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ValidationMemberControllerV3 {

    private final MemberServiceImpl memberService;

    @GetMapping("/memberInsertForm")
    public String memberInsert(Model model) {
        log.info("memberInsertForm IN");
        model.addAttribute("member", new MemberSaveForm());
        return "html/memberInsertForm";
    }

    @PostMapping("/memberInsert")
    public String memberInsert(@Validated @ModelAttribute("member") MemberSaveForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (memberService.nickNameDuplicateCheck(form.getNickName())) {
            bindingResult.rejectValue("nickName", "duplicated");
        }

        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
        if (form.getPassword() != null && form.getPasswordConfirm() != null) {
            String memberPassword = form.getPassword();
            String memberPasswordConfirm = form.getPasswordConfirm();
            if (memberPassword != memberPasswordConfirm) {
                bindingResult.reject("passwordSame");
            }
        }


        //검증에 실패하면 다시 입력 폼으로!
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "html/memberInsertForm";
        }

        // 성공 로직 -> 수정됨 
        Member member = new Member(form.getNumber(), form.getUserEmail(), form.getNickName(), form.getPassword(), form.getPasswordConfirm(), form.isPrivacyCheck(), form.isTermsCheck());
        memberService.join(member);
        return "redirect:/";
    }

}
```

***```LoginService```***

```java
@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @return null 로그인 실패
     */
    public Member login(String userEmail, String password) {
        return memberRepository.findByEmail(userEmail)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

}
```

***```LoginController```***

```java
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") MemberLoginForm form) {
        return "html/login";
    };

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "html/login";
        }

        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());

        if (loginMember == null) {
            log.info("errors = {}", bindingResult);
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "html/login";
        }

        //로그인 성공 처리 TODO

        return "redirect:/";
    }
}
```

:monkey: 로직을 작성했고 아래와 같이 잘 작동하는것을 확인할 수 있다.:monkey_face:

![image](https://user-images.githubusercontent.com/76586084/185922911-f36d2717-a2f4-4276-84cb-7d3d3748e03d.png)



## Day 11

---

##### 로그인 처리하기 - 쿠키 사용

```java
package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.web.login.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

```

로그인에 성공하면 쿠키를 생성하고 ***```HttpServletResponse```*** 에 담는다. 쿠키 이름은 ***```memberNickName```*** 이고, 값은 ***```id```*** 를 담아둔다. 웹 브라우저는 종료 전까지 회원의 ***```id```*** 를 서버에 계속 보내줄 것이다.



#### "실행"

크롬 브라우저를 통해 HTTP 응답 헤더에 쿠기가 추가된 것을 확인 :open_mouth::exclamation:

![image-20220823144537110](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220823144537110.png)



***```Request Headers```***

![image-20220823144750205](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20220823144750205.png)

들어왔다! :open_mouth::exclamation:



이제 로그인 처리를 고려한 홈 화면을 만들어보자.

***```HomeController```***

```java
package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

//    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/")
    public String home(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        log.info("memer={}", model);
        return "loginHome";
    }
}
```

![image](https://user-images.githubusercontent.com/76586084/186150441-73379a00-3794-49ed-b90b-62adbbe2d121.png)

화면에 로그인한 사용자의 ***```nickName```*** 과 ***```logout button```*** 을 표시해주었다.



***```logoutbutton```*** 을 사용해 cookie를 만료시키고 로그아웃을 시켜보자

***```LoginController```***

```java
package hello.login.domain.login;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
```



##### 하지만 이러한 쿠키를 이용한 로그인 기능에는 보안상의 큰 문제점이 있다. 

##### "보안 문제"

- 쿠키 값은 임의로 변경 가능
  - 클라이언트가 쿠키를 강제로 변경할시 다른 사용자가 됨
  - 실제 웹 브라우저 개발자 모드에서 변경 가능
- 쿠키에 보관된 정보는 훔쳐갈 수 있다.
- 해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.



##### "대안"

- 쿠키에 중요한 값을 노출하지 않고, 사용자 별로 예측 불가능한 임의의 토큰(랜덤 값)을 노출하고, 서버에서 토큰과 사용자 id를 매핑해서 인식한다. 그리고 서버에서 토큰을 관리한다.
- 토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.
- 해커가 토큰을 텅거다고 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게(30분)유지한다. 또는 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거하면 된다.







## Day 12

---

#### 로그인 처리하기 - 세션 동작 방식

##### "목표"

앞서 쿠키에 중요한 정보를 보관하는 방법은 여러가지 보안 이슈가 있었다. 이 문제를 해결하려면 경국 중요한 정보를 모두 서버에 저장해야 한다. 그리고 클라이언트와 서버는 추정 불가능한 임의의 식별자 값으로 연결해야 한다.



##### 세션 동장 방식

세션을 어떻게 개발할지 먼저 개념을 이해해보자.

 ![image](https://user-images.githubusercontent.com/76586084/186357784-aaef9b75-08b3-476b-9340-a6668e5a1378.png)

- 사용자가 ***```loginId```*** , ***```password```*** 정보를 전달하면 서버에서 해당 사용자가 맞는지 확인한다.

##### "세션 생성"

![image](https://user-images.githubusercontent.com/76586084/186358345-f9eae48f-0874-4308-876b-b68bf12a95a5.png)

- 추정 불가능한 sessionId를 생성한다.
- ***```UUID```*** 는 추정이 불가능하다.
  - ***```Cookie : mySessionId = zz0101xx-bab9-4b92-9b32-dadb280f4b61```***
  - 생성된 세션 ID와 세션에 보관할 값('memberA')을 서버의 세션 저장소에 보관한다.



##### "세션 Id를 응답 쿠키로 전달"

![image](https://user-images.githubusercontent.com/76586084/186360704-fa9ce5d9-04ca-4271-abb5-e198b2a0d98e.png)

##### "클라이언트와 서버는 결국 쿠키로 연결이 되어야 한다."

- 서버는 클라이언트에 ***```mySessionId```*** 라는 이름으로 세션ID만 쿠키에 담아서 전달한다.
- 클라이언트는 쿠키 저장소에 ***```mySessinoId```*** 쿠키를 보관한다.



##### "중요" :monkey::exclamation: 

- 여기서 중요한 포인트는 회원과 관련된 정보는 전혀 클라이언트에 전달하지 않는다는 것이다.
- 오직 추정 불가능한 세션 ID만 쿠리를 통해 클라이언트에 전달된다.



##### "클라이언트의 세션id 쿠키 전달"

![image](https://user-images.githubusercontent.com/76586084/186364018-7066bdf4-e443-41e0-b5f2-fdb8cbe081f0.png)

- 클라이언트는 요청시 항상 ***```mySessionId```*** 쿠키를 전달한다.
- 서버에서 클라이언트가 전달한 ***```mySessionId```*** 쿠키 정보를 세션 저장소를 조회해서 로그인시 보관한 세션 정보를 사용한다.



##### :scream_cat: "정리" :satellite:

세션을 사용해서 서버에서 중요한 정보를 관리하게 되었다. 덕분에 다음과 같은 보안 문제를 해결할 수 있다.

- 쿠키 값을 변조 가능 -> 예상 불가능한 복잡한 세션 id를 사용한다.
- 쿠키에 보관하는 정보는 클라이언트 해킹시 털릴 가능성이 있다. -> 세션 id가 털려도 여기에는 중요한 정보가 없다.
- 쿠키 탈취 후 사용 -> 해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 세션 만료 시간을 짧게(30분) 또는 해킹이 의심되는 경우 서버에서 해당 세션을 강제로 제거한다.



#### 로그인 처리하기 - 세션을 직접 만들어서 해보자

##### 세션 3기능

##### 1. 세션 생성

- sessionId생성 (임의의 추정 불가능한 랜덤 값)
- 세션 저장소에 sessionId와 보관할 값 저장

##### 2. 세션 조회

- 클라이언트가 요청한 sessionId 쿠키의 값으로, 세션 저장소에 보관한 값 조회

##### 3. 세션 만료

- 클라이언트가 요청한 sessionId 쿠키의 값으로, 세션 저장소에 보관한 sessionid 값 제거



***```SessionManager```***

```java
/**
 * 세션 관리
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     *  * sessionId 생성 (임의의 추정 불가능한 랜덤 값)
     *  * session 저장소에 sessionId와 보관할 값 저장
     *  * sessionId로 응답 쿠키를 생성해서 클라이언트에 전달
     */
    public void createSession(Object value, HttpServletResponse response) {

        //세션 id를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        //쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }
}
```



테스트 코드를 작성해보았다.

```java
public class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest() {

        //세션 생성
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        sessionManager.createSession(member, response);

        //요청에 응답 쿠키 저장
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        //세션 조회
        Object result = sessionManager.getSession(request);
        Assertions.assertThat(result).isEqualTo(member);

        //세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        Assertions.assertThat(expired).isNull();
    }
}
```



만들어진 ***```SessionManager```*** 를 통해 ***```LoginController```*** 와 ***```HomeController```*** 를 Refactoring해보자

***```LoginController```***

```java
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") MemberLoginForm form) {
        return "html/login";
    }

    ;

    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "html/login";
        }

        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());

        if (loginMember == null) {
            log.info("errors = {}", bindingResult);
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "html/login";
        }

        //로그인 성공 처리

        //세션 관리자를 통해 세션을 생성하고, 화면 데이터 보관
        sessionManager.createSession(loginMember, response);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
```



***```HomeController```***

```
@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberServiceImpl memberService;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String IndexPage(Model model) {
        log.info("Index Page");
        return "index";
    }


    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        //세션 관리자에 저장된 정보를 조회회
        Member member = (Member) sessionManager.getSession(request);
        //로그인
        if (member == null) {
            return "index";
        }

        model.addAttribute("member", member);
        log.info("model = {}", model);
        return "homeIndex";
    }

    }
```





### 로그인 처리하기 - 서블릿 HTTP 세션1

이제 서블릿이 제공하는 ***```HttpSession```*** 을 활용해 ***```SessionManager```*** 을 구현해 보자 (기본적인 로직은 동일하다.)
서블릿을 통해 ***```HttpSession```*** 을 생성하면 다음과 같은 쿠키를 생성한다. 쿠키 이름이 ***```JESSIONID```*** 이고, 값은 추정 불가능한 랜덤 값이다.

***```Cookie: JESSIONID=58E9869SDFS986987,,,```***

***```LoginController```***

```java
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") MemberLoginForm form) {
        return "html/login";
    }

    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "html/login";
        }

        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());

        if (loginMember == null) {
            log.info("errors = {}", bindingResult);
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "html/login";
        }

        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 관리
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }


    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
```

***```HomeContoller```***

```java
@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberServiceImpl memberService;
    private final SessionManager sessionManager;

    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "index";
        }
        
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);


        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "index";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        log.info("model = {}", model);
        return "homeIndex";
    }

    }
    }
```



여기서 한발자국 더 나가 ***```@SessionAttribute```*** 를 이용해 보자

***```HomeController```***

```java
@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberServiceImpl memberService;
    private final SessionManager sessionManager;


    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {


        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "index";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        log.info("model = {}", model);
        return "homeIndex";
    }
    }
```



그런데 처음 로그인을 하면

![image](https://user-images.githubusercontent.com/76586084/186408965-1cd6804b-f477-4bcd-85a4-870753e8618c.png)

이렇게 jsessionId가 들어가고 response에도 setCookie가 들어감

이것은 웹 브라우저가 쿠키를 지원하지 않을 때 쿠키 대신 URL을 통해서 세션을 유지하는 방법이다. 이 방법을 사용하려면 URL에 같은 값을 계속 전달해야 한다. (이 방법을 사용하길 원치 않음)



application.properties에 

```
server.servlet.session.tracking-modes==cookie
```

를 추가한다.

































































































 
