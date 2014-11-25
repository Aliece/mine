package org.aliece.mine.config;

public interface ServerCapabilities {
	// new more secure passwords
	int CLIENT_LONG_PASSWORD = 1;

	// Found instead of affected rows
	// �����ҵ���ƥ�䣩�������������Ǹı��˵�������
	int CLIENT_FOUND_ROWS = 2;

	// Get all column flags
	int CLIENT_LONG_FLAG = 4;

	// One can specify db on connect
	int CLIENT_CONNECT_WITH_DB = 8;

	// Don't allow database.table.column
	// ���������ݿ���.����.�������������﷨�����Ƕ���ODBC�����á�
	// ��ʹ���������﷨ʱ�����������һ�����������һЩODBC�ĳ�������bug��˵�����õġ�
	int CLIENT_NO_SCHEMA = 16;

	// Can use compression protocol
	// ʹ��ѹ��Э��
	int CLIENT_COMPRESS = 32;

	// Odbc client
	int CLIENT_ODBC = 64;

	// Can use LOAD DATA LOCAL
	int CLIENT_LOCAL_FILES = 128;

	// Ignore spaces before '('
	// �����ں�������ʹ�ÿո����к���������Ԥ���֡�
	int CLIENT_IGNORE_SPACE = 256;

	// New 4.1 protocol This is an interactive client
	int CLIENT_PROTOCOL_41 = 512;

	// This is an interactive client
	// ����ʹ�ùر�����֮ǰ�Ĳ��������ʱ�������������ǵȴ���ʱ������
	// �ͻ��˵ĻỰ�ȴ���ʱ������Ϊ������ʱ������
	int CLIENT_INTERACTIVE = 1024;

	// Switch to SSL after handshake
	// ʹ��SSL��������ò�Ӧ�ñ�Ӧ�ó������ã���Ӧ�����ڿͻ��˿��ڲ������õġ�
	// �����ڵ���mysql_real_connect()֮ǰ����mysql_ssl_set()���������á�
	int CLIENT_SSL = 2048;

	// IGNORE sigpipes
	// ��ֹ�ͻ��˿ⰲװһ��SIGPIPE�źŴ�������
	// ����������ڵ�Ӧ�ó����Ѿ���װ�ô�������ʱ��������䷢����ͻ��
	int CLIENT_IGNORE_SIGPIPE = 4096;

	// Client knows about transactions
	int CLIENT_TRANSACTIONS = 8192;

	// Old flag for 4.1 protocol
	int CLIENT_RESERVED = 16384;

	// New 4.1 authentication
	int CLIENT_SECURE_CONNECTION = 32768;

	// Enable/disable multi-stmt support
	// ֪ͨ�������ͻ��˿��Է��Ͷ�����䣨�ɷֺŷָ���������ñ�־Ϊû�б����ã��������ִ�С�
	int CLIENT_MULTI_STATEMENTS = 65536;

	// Enable/disable multi-results
	// ֪ͨ�������ͻ��˿��Դ����ɶ������ߴ洢����ִ�����ɵĶ�������
	// ����CLIENT_MULTI_STATEMENTSʱ�������־�Զ��ı��򿪡�
	int CLIENT_MULTI_RESULTS = 131072;
}
