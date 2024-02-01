# Swing 라이브러리 사용한 미니 게임 구현

<img src='https://github.com/gksrbgks2021/minigame/assets/39733405/44020d96-93ec-4ffc-ae8b-ee2ebf51216b'/>

# 시연 영상
[![Video Label](http://img.youtube.com/vi/RY4GhfCQOag/0.jpg)](https://youtu.be/RY4GhfCQOag)

# 사용 SKill
- java
- Swing
- jl1.0.jar (mp3 파일응 재생하기 위한 외부 라이브러리)

# 게임 요약
- 사용자는 4가지 미니게임을 플레이할 수 있다.
- 멀티 스레드 를 사용하여, 객체간 충돌 탐지 와 키보드 입력 이벤트 처리를 받아 게임을 진행한다.
- 표적 맞추기 게임, 점프 게임 , 장애물 피하기 , 리듬 게임으로 구성되어 있다.
- LinkedBlockingQueue 를 사용하여, 객체가 생성되고, 삭제될때 Blocking하여 Thread-Safety한 자료구조를 사용하였다. 

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/f3e592af-e9d9-402b-9809-ba52601a0f2e)

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/09eaf76b-7754-451c-97b3-ec01a5d87e6a)

# 핵심 코드
### 타이머로 스레드 생성, 실행
```java

t1 = new Timer(50, e -> {
			CheckCrash(); //충돌 탐지하는 함수
		});
		t1.setInitialDelay(500); // 0.5초 뒤에 타이머를 시작합니다.
		t2 = new Timer(30, e -> { // move.
			x -= (mSpeed);
			setLocation(x, y);
			count++;
		});

```
### 충돌 탐지
- 특정 시간마다 충돌 지점 "poly" 점을 이동시켜, 해당 충돌 지점에 맟닿으면, 라이프가 줄어듭니다.
```java
public void CheckCrash() {// 충돌을 체크합니다.
		if (!lock) {
			lock = true;
			if (iscrash)// 충돌했으면 아무것도 안함.
			{
				t1.stop();
				return;
			}
			if (type == 0) {
				r[0].setLocation(x, y);
				r[1].setBounds(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
				if (r[0].intersects(r[1])) // 충돌했으면
				{
					crash();
				}
			}

			if (type == 1) {
				r[1].setBounds(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
				for (int i = 0; i < 5; i++) {// 드릴 꼭짓점.
					if (r[1].contains(polx[i] - (count * mSpeed), poly[i])) {
						crash();
						break;
					}
				}
			}
			lock = false;
		}
	}
```
# 스크린샷

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/5f6631e6-8cd7-4c37-a4ec-2fd93bc29693)

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/fdf1df84-f113-456a-86ec-612e438ccd57)

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/a86da7d8-d4e9-4cc8-83c4-98d0d20a0c2b)

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/b06671b9-39dd-43a3-b9cd-16557ed02c34)

![image](https://github.com/gksrbgks2021/minigame/assets/39733405/9efac1c9-f92d-467b-8691-800ab6ad9a51)

# 느낀점

- 프로젝트를 만들면서 클래스를 하나 만들고, 타이머를 추가할때마다 충돌, 메소드 중복호출이 일어나 디버깅 하는데 시간을 많이 썼다. 
- 운영체제 이론에서 배웠던 스레드 간 동기화 문제 , 자바 이벤트 스택 처리 문제를 직접 겪고 해결하는 과정이 흥미로웠다.
- 입력 이벤트와 화면 렌더링을 하나의 스레드에서 동작하는 reactive 하게 구현하지 않아, 성능 저하가 발생하였다. 이를 해결하려면 Double buffering 기법으로 렌더링을 담당하는 하나의 스레드한테 화면 변동 이벤트를 전달해야한다. 이 해결법은 코드 전체를 리팩토링 해야하는 데 유지보수 비용이 커서 문제점을 파악 하는 것으로 끝냈다.
