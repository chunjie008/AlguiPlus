#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <dirent.h>
#include <fcntl.h>
#include <vector>
#include <pthread.h>
#include <unistd.h>
//by皓月当空 - 添加更多的内存类型
#define RANGE_ALL 0//全部内存😥
#define RANGE_C_HEAP 2//ch内存😥
#define RANGE_JAVA_HEAP 8//jh内存😥
#define RANGE_C_ALLOC 3//ca内存😥
#define RANGE_C_DATA 4//cd内存😥
#define RANGE_C_BSS 1//cb内存😥
#define RANGE_ANONYMOUS 7//a内存😥
#define RANGE_B_BAD 6//b内存😥
#define RANGE_CODE_SYSTEM 10//xs内存😥
#define RANGE_VIDEO 11//v内存😥
#define RANGE_STACK 5//s内存😥
#define RANGE_ASHMEM 12//as内存😥
#define RANGE_CODE_APP 9//xa内存😥
#define RANGE_OTHER 13//o内存
#define TYPE_DWORD 1//d类型
#define TYPE_FLOAT 2//f类型
#define TYPE_DOUBLE 3//e类型
#define DWORD int32_t
#define FLOAT float
#define DOUBLE double
	struct Data
{
	int pid;
	int range;
} data;


static bool freeze;


struct Table
{
	char *value;
	unsigned long addr;
	int type;
};


using namespace std;


vector < unsigned long >sresult;
vector < unsigned long >oresult;
vector < Table > item;


int getPackageNamePid(char *name);	// 获取包名pid
void setPackageName(char *name);	// 设置包名
void setRange(int range);		// 设置内存范围
unsigned long getModuleAddress(char *name);	// 获取基址
void RangeMemorySearch(char *value, int type);	// 内存搜索
void RangeMemorySearchDword(int fd, DWORD value, unsigned long addr, int size);	// 内存搜索D类型
void RangeMemorySearchFloat(int fd, FLOAT value, unsigned long addr, int size);	// 内存搜索D类型
void RangeMemorySearchDouble(int fd, DOUBLE value, unsigned long addr, int size);	// 内存搜索D类型
void MemoryOffset(char *value, int type, unsigned long offset);	// 内存偏移搜索
void MemoryOffsetDword(int fd, int value, unsigned long offset);	// 内存偏移搜索D类型
void MemoryOffsetFloat(int fd, float value, unsigned long offset);	// 内存偏移搜索F类型
void MemoryOffsetDouble(int fd, double value, unsigned long offset);	// 内存偏移搜索E类型
void MemoryWrite(char *value, int type, unsigned long offset);	// 内存写入
void MemoryWriteDword(int fd, int value, unsigned long offset);	// 内存写入D类型
void MemoryWriteFloat(int fd, float value, unsigned long offset);	// 内存写入F类型
void MemoryWriteDouble(int fd, double value, unsigned long offset);	// 内存写入E类型
void setValue(char *value, unsigned long addr, int type);	// 设置数值
void setValueDword(int fd, int value, unsigned long addr);	// 设置D类型数值
void setValueFloat(int fd, float value, unsigned long addr);	// 设置F类型数值
void setValueDouble(int fd, double value, unsigned long addr);	// 设置E类型数值
DWORD readDword(unsigned long);	// 读取D类型数值
FLOAT readFloat(unsigned long);	// 读取F类型数值
unsigned long readLong(unsigned long);	// 读取指针
void clearResult();				// 清空搜索结果
int getResultCount();			// 获取搜索结果数量
unsigned long *getResult(int count);	// 获取搜索结果地址
void *thread(void *arg);		// 冻结线程
void addListItem(char *value, unsigned long addr, int type);	// 添加一个项目
void removeListItem(unsigned long addr);	// 移除一个项目
void startFreeze();				// 启动冻结线程
void stopFreeze();				// 关闭冻结线程


int getPackageNamePid(char *name)
{
	int pid = -1;
	char cmdline[1024];
	char cmdname[1024];
	DIR *dir = opendir("/proc");
	struct dirent *entry;
	while ((entry = readdir(dir)) != NULL)
	{
		int id = atoi(entry->d_name);
		if (id != 0)
		{
			snprintf(cmdline, sizeof(cmdline), "/proc/%d/cmdline", id);
			FILE *fp = fopen(cmdline, "r");
			if (fp != NULL)
			{
				fgets(cmdname, sizeof(cmdname), fp);
				fclose(fp);
				if (strcmp(cmdname, name) == 0)
				{
					pid = id;
					break;
				}
			}
		}
	}
	closedir(dir);
	return pid;
}


void setPackageName(char *name)
{
	data.pid = getPackageNamePid(name);
}


void setRange(int range)
{
	data.range = range;
}


unsigned long getModuleAddress(char *module)
{
	char mapPath[1024];
	char mapLine[1024];
	unsigned long addr;
	sprintf(mapPath, "/proc/%d/maps", data.pid);
	FILE *fp = fopen(mapPath, "r");
	if (fp)
	{
		while (fgets(mapLine, sizeof(mapLine), fp))
		{
			if (strstr(mapLine, module))
			{
				addr = strtoul(mapLine, NULL, 16);
				break;
			}
		}
	}
	fclose(fp);
	return addr;
}


void range_java_heap(char *value, int type)
{
}


void range_c_heap(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "[heap]"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
				}
			}
		}
	}
}


void range_c_alloc(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "[anon:libc_malloc]"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}

void range_all(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				
			}
		}
	}
}

void range_anonymous(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			char module[256];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw"))
				{
					memset(module, 0, sizeof(module));
					sscanf(mapLine, "%lx-%lx %*s %*lx %*s %*d%s", &start, &end, &module);
					if (strcmp(module, "") == 0)
					{
						int size = end - start;
						switch (type)
						{
						case TYPE_DWORD:
							RangeMemorySearchDword(fd, atoi(value), start, size);
							break;
						case TYPE_FLOAT:
							RangeMemorySearchFloat(fd, atof(value), start, size);
							break;
						case TYPE_DOUBLE:
							RangeMemorySearchDouble(fd, atof(value), start, size);
							break;
						}
					}
				}
			}
		}
	}
}


void range_video(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "/dev/kgsl-3d0"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}

//xa内存
void range_code_app(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "r-xp") && strstr(mapLine, "/data/app/"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}
//cd内存
void range_c_data(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "/data/app/"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}
//cb内存
void range_c_bss(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "[anon:.bss]"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}
//b内存
void range_bad(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "kgsl-3d0"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}
//s内存
void range_stack(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "[stack]"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}
//as内存
void range_ashmem(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "/dev/ashmem/") && strstr(mapLine,"dalvik"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}

void range_other(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "[anon:thread signal stack]"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}

void range_code_system(char *value, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		char mapPath[1024];
		sprintf(mapPath, "/proc/%d/maps", data.pid);
		FILE *fp = fopen(mapPath, "r");
		if (fp)
		{
			char mapLine[1024];
			unsigned long start;
			unsigned long end;
			while (fgets(mapLine, sizeof(mapLine), fp))
			{
				if (strstr(mapLine, "rw") && strstr(mapLine, "/system"))
				{
					sscanf(mapLine, "%lx-%lx", &start, &end);
					int size = end - start;
					switch (type)
					{
					case TYPE_DWORD:
						RangeMemorySearchDword(fd, atoi(value), start, size);
						break;
					case TYPE_FLOAT:
						RangeMemorySearchFloat(fd, atof(value), start, size);
						break;
					case TYPE_DOUBLE:
						RangeMemorySearchDouble(fd, atof(value), start, size);
						break;
					}
				}
			}
		}
	}
}





void RangeMemorySearch(char *value, int type)
{
	switch (data.range)
	{
		break;
	case RANGE_C_HEAP:
		range_c_heap(value, type);
		break;
	case RANGE_C_ALLOC:
		range_c_alloc(value, type);
		break;
	case RANGE_ANONYMOUS:
		range_anonymous(value, type);
		break;
	case RANGE_VIDEO:
		range_video(value, type);
		break;
    case RANGE_ALL:
        range_all(value,type);
        break;
    case RANGE_CODE_APP:
        range_code_app(value, type);
        break;
        case RANGE_JAVA_HEAP:
        range_java_heap(value, type);
        break;
        case RANGE_C_DATA:
        range_c_data(value, type);
        break;
        case RANGE_C_BSS:
        range_c_bss(value, type);
        break;
        case RANGE_B_BAD:
        range_bad(value, type);
        break;
        case RANGE_CODE_SYSTEM:
        range_code_system(value, type);
        break;
        case RANGE_STACK:
        range_stack(value, type);
        break;
        case RANGE_ASHMEM:
        range_ashmem(value, type);
        break;
        case RANGE_OTHER:
        range_other(value, type);
        break;
	}
}


void RangeMemorySearchDword(int fd, DWORD value, unsigned long addr, int size)
{
	void *buf = (void *)malloc(size);
	int ret = pread64(fd, buf, size, addr);
	if (ret == size)
	{
		int index = 0;
		while (index < size)
		{
			if (*(DWORD *) (buf + index) == value)
			{
				sresult.push_back(addr + index);
			}
			index += sizeof(DWORD);
		}
	}
	free(buf);
}


void RangeMemorySearchFloat(int fd, FLOAT value, unsigned long addr, int size)
{
	void *buf = (void *)malloc(size);
	int ret = pread64(fd, buf, size, addr);
	if (ret == size)
	{
		int index = 0;
		while (index < size)
		{
			if (*(FLOAT *) (buf + index) == value)
			{
				sresult.push_back(addr + index);
			}
			index += sizeof(FLOAT);
		}
	}
	free(buf);
}


void RangeMemorySearchDouble(int fd, DOUBLE value, unsigned long addr, int size)
{
	void *buf = (void *)malloc(size);
	int ret = pread64(fd, buf, size, addr);
	if (ret == size)
	{
		int index = 0;
		while (index < size)
		{
			if (*(DOUBLE *) (buf + index) == value)
			{
				sresult.push_back(addr + index);
			}
			index += sizeof(DOUBLE);
		}
	}
	free(buf);
}


void MemoryOffset(char *value, int type, unsigned long offset)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		switch (type)
		{
		case TYPE_DWORD:
			MemoryOffsetDword(fd, atoi(value), offset);
			break;
		case TYPE_FLOAT:
			MemoryOffsetFloat(fd, atof(value), offset);
			break;
		case TYPE_DOUBLE:
			MemoryOffsetDouble(fd, atof(value), offset);
			break;
		}
	}
	close(fd);
}


void MemoryOffsetDword(int fd, DWORD value, unsigned long offset)
{
	int size = sizeof(DWORD);
	void *buf = (void *)malloc(size);
	oresult.clear();
	for (int i = 0; i < sresult.size(); i++)
	{
		int ret = pread64(fd, buf, size, sresult[i] + offset);
		if (ret == size)
		{
			if (*(DWORD *) buf == value)
			{
				oresult.push_back(sresult[i]);
			}
		}
	}
	sresult.clear();
	for (int i = 0; i < oresult.size(); i++)
	{
		sresult.push_back(oresult[i]);
	}
	free(buf);
}


void MemoryOffsetFloat(int fd, FLOAT value, unsigned long offset)
{
	int size = sizeof(FLOAT);
	void *buf = (void *)malloc(size);
	oresult.clear();
	for (int i = 0; i < sresult.size(); i++)
	{
		int ret = pread64(fd, buf, size, sresult[i] + offset);
		if (ret == size)
		{
			if (*(FLOAT *) buf == value)
			{
				oresult.push_back(sresult[i]);
			}
		}
	}
	sresult.clear();
	for (int i = 0; i < oresult.size(); i++)
	{
		sresult.push_back(oresult[i]);
	}
	free(buf);
}


void MemoryOffsetDouble(int fd, DOUBLE value, unsigned long offset)
{
	int size = sizeof(DOUBLE);
	void *buf = (void *)malloc(size);
	oresult.clear();
	for (int i = 0; i < sresult.size(); i++)
	{
		int ret = pread64(fd, buf, size, sresult[i] + offset);
		if (ret == size)
		{
			if (*(DOUBLE *) buf == value)
			{
				oresult.push_back(sresult[i]);
			}
		}
	}
	sresult.clear();
	for (int i = 0; i < oresult.size(); i++)
	{
		sresult.push_back(oresult[i]);
	}
	free(buf);
}


void MemoryWrite(char *value, int type, unsigned long offset)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_WRONLY);
	if (fd != -1)
	{
		switch (type)
		{
		case TYPE_DWORD:
			MemoryWriteDword(fd, atoi(value), offset);
			break;
		case TYPE_FLOAT:
			MemoryWriteFloat(fd, atof(value), offset);
			break;
		case TYPE_DOUBLE:
			MemoryWriteDouble(fd, atof(value), offset);
			break;
		}
	}
	close(fd);
}


void MemoryWriteDword(int fd, DWORD value, unsigned long offset)
{
	for (int i = 0; i < sresult.size(); i++)
	{
		pwrite64(fd, &value, sizeof(DWORD), sresult[i] + offset);
	}
}


void MemoryWriteFloat(int fd, FLOAT value, unsigned long offset)
{
	for (int i = 0; i < sresult.size(); i++)
	{
		pwrite64(fd, &value, sizeof(FLOAT), sresult[i] + offset);
	}
}

void MemoryWriteDouble(int fd, DOUBLE value, unsigned long offset)
{
	for (int i = 0; i < sresult.size(); i++)
	{
		pwrite64(fd, &value, sizeof(DOUBLE), sresult[i] + offset);
	}
}


void setValue(char *value, unsigned long addr, int type)
{
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_WRONLY);
	if (fd != -1)
	{
		switch (type)
		{
		case TYPE_DWORD:
			setValueDword(fd, atoi(value), addr);
			break;
		case TYPE_FLOAT:
			setValueFloat(fd, atof(value), addr);
			break;
		case TYPE_DOUBLE:
			setValueDouble(fd, atof(value), addr);
			break;
		}
	}
	close(fd);
}


void setValueDword(int fd, DWORD value, unsigned long addr)
{
	pwrite64(fd, &value, sizeof(DWORD), addr);
}


void setValueFloat(int fd, FLOAT value, unsigned long addr)
{
	pwrite64(fd, &value, sizeof(FLOAT), addr);
}


void setValueDouble(int fd, DOUBLE value, unsigned long addr)
{
	pwrite64(fd, &value, sizeof(DOUBLE), addr);
}


DWORD readDword(unsigned long addr)
{
	int size = sizeof(DWORD);
	void *buf = (void *)malloc(size);
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd > 0)
	{
		pread64(fd, buf, size, addr);
	}
	free(buf);
	close(fd);
	return *(DWORD *) buf;
}


FLOAT readFloat(unsigned long addr)
{
	int size = sizeof(FLOAT);
	void *buf = (void *)malloc(size);
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		pread64(fd, buf, size, addr);
	}
	free(buf);
	close(fd);
	return *(FLOAT *) buf;
}


DOUBLE readDouble(unsigned long addr)
{
	int size = sizeof(DOUBLE);
	void *buf = (void *)malloc(size);
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		pread64(fd, buf, size, addr);
	}
	free(buf);
	close(fd);
	return *(DOUBLE *) buf;
}


unsigned long readLong(unsigned long addr)
{
	int size = sizeof(DWORD);
	void *buf = (void *)malloc(size);
	char memPath[1024];
	sprintf(memPath, "/proc/%d/mem", data.pid);
	int fd = open(memPath, O_RDONLY);
	if (fd != -1)
	{
		pread64(fd, buf, size, addr);
	}
	free(buf);
	close(fd);
	return *(unsigned long *)buf;
}


void clearResult()
{
	sresult.clear();
}


int getResultCount()
{
	return sresult.size();
}


unsigned long *getResult(int count)
{
	unsigned long *result = (unsigned long *)malloc(count);
	for (int i = 0; i < count; i++)
	{
		result[i] = sresult[i];
	}
	free(result);
	return result;
}


void *thread(void *arg)
{
	for (;;)
	{
		int count = item.size();
		if (count == 0)
		{
			break;
		}
		for (int i = 0; i < count; i++)
		{
			setValue(item[i].value, item[i].addr, item[i].type);
			if (!freeze)
			{
				break;
			}
		}
	}
}


void addListItem(char *value, unsigned long addr, int type)
{
	Table table;
	table.value = value;
	table.addr = addr;
	table.type = type;
	item.push_back(table);
}


void removeListItem(unsigned long addr)
{
	for (int i = 0; i < item.size(); i++)
	{
		if (addr == item[i].addr)
		{
			item.erase(item.begin() + i);
			break;
		}
	}
}


void startFreeze()
{
	if (!freeze)
	{
		if (item.size() > 0)
		{
			freeze = true;
			pthread_t t;
			pthread_create(&t, NULL, thread, NULL);
		}
	}
}


void stopFreeze()
{
	if (freeze)
	{
		if (item.size() == 0)
		{
			freeze = false;
		}
	}
}
