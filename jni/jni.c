#include <unistd.h>  
#include <sys/stat.h>  
#include <sys/time.h>  
#include <stdlib.h>  
#include <fcntl.h>  


/*���ļ����������ھʹ���������ֵΪһ���ļ����*/  
int file_open(const char *filename)  
{  
    int file_handle;  
    int flags = 0100|02; 
   
    file_handle = open(filename, flags, 0777);  
    if (file_handle == -1)  
        return -1;  
  
    return file_handle;  
}  

/*�ļ�д���������ɹ��ͻ᷵��ʵ��д����ֽ��������д�����ʱ�򷵻�-1*/
 int file_write(int file_handle, const unsigned char *buf, int size)  
{  
      
    return write(file_handle, buf, size);  
}  
  
/*�ر��ļ�����*/
 int file_close(int file_write)  
{  
     
    return close(file_write);  
}  

/*���ܺ���*/
void decrypt(unsigned char *buf, int size)
{
    int i;
    unsigned char key = 0xEA;
    
    for(i=0;i<size;i++){
    	    buf[i]=buf[i]^key; 
    }
    
}
