#include <unistd.h>  
#include <sys/stat.h>  
#include <sys/time.h>  
#include <stdlib.h>  
#include <fcntl.h>  


/*打开文件，若不存在就创建，返回值为一个文件句柄*/  
int file_open(const char *filename)  
{  
    int file_handle;  
    int flags = 0100|02; 
   
    file_handle = open(filename, flags, 0777);  
    if (file_handle == -1)  
        return -1;  
  
    return file_handle;  
}  

/*文件写操作，若成功就会返回实际写入的字节数。当有错误发生时则返回-1*/
 int file_write(int file_handle, const unsigned char *buf, int size)  
{  
      
    return write(file_handle, buf, size);  
}  
  
/*关闭文件操作*/
 int file_close(int file_write)  
{  
     
    return close(file_write);  
}  

/*解密函数*/
void decrypt(unsigned char *buf, int size)
{
    int i;
    unsigned char key = 0xEA;
    
    for(i=0;i<size;i++){
    	    buf[i]=buf[i]^key; 
    }
    
}
