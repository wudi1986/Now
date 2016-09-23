package com.yktx.group.conn;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company:
 * </p>
 * @author not attributable
 * @version 1.0
 */
public class HttpURLParam
{
    private String _host;
    private String _port;
    private String _path;

    public HttpURLParam(String aurl)
    {
        int idx = aurl.indexOf("://");
        if (idx == -1)
        {
            idx = 0;
        }
        else
        {
            idx += 3;
        }
        int idx2 = aurl.indexOf('/', idx);
        _host = aurl.substring(idx, idx2);
        int idx3 = _host.indexOf(':');
        if (idx3 == -1)
        {
            _port = "80";
        }
        else
        {
            _port = _host.substring(idx3 + 1);
            _host = _host.substring(0, idx3);
        }
        _path = aurl.substring(idx2);
    }

    public String getHost()
    {
        return _host;
    }

    public String getPort()
    {
        return _port;
    }

    public String getPath()
    {
        return _path;
    }
}
