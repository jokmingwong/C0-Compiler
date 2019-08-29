
使用OpenMP对短代码进行测试，源代码如下，不加OpenMP把编译选项和预处理注释掉就行了
```cpp
#include<iostream>
#include<omp.h>
#include<cstdlib>
#include<time.h>
#include<random>
const int MAX=114514;
using namespace std;
int str[MAX];
int main(){
        srand((unsigned)time(NULL));
        clock_t start_time=clock();
        #pragma omp parallel for
        for(unsigned i=0;i<MAX;i++){
                str[i]=rand()%MAX;      
        } 
        clock_t end_time=clock();
        cout<<"Running Time is:"<<static_cast<double>(end_time-start_time)/CLOCKS_PER_SEC*1000<<"ms"<<endl;   
        return 0;
}
```
分别各两次编译运行，输出运行时间

```
第一次加上OpenMP
Running Time is:93.75ms
第一次不加OpenMP
Running Time is:0ms
第二次加OpenMP
Running Time is:93.75ms
第二次不加OpenMP
Running Time is:0ms 
```

这说明在数据量比较小的情况下，使用OpenMP带来CPU处理器之间通讯的延迟要远比不用OpenMP的串行程序要多。所以不是见到可并行化的代码块就一定要并行化，是否使用OpenMP取决于代码块的计算时间。