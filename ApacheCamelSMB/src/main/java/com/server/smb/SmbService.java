package com.server.smb;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@Service
public class SmbService {

    private final CIFSContext context;

    public SmbService() throws CIFSException {
        Properties prop = new Properties();
        CIFSContext base = new BaseContext(new PropertyConfiguration(prop));
        this.context = base.withCredentials(
                new NtlmPasswordAuthenticator("smbuser", "smbpass")
        );
    }

    public void uploadFile(String filename, InputStream fileStream) throws Exception {
        String smbUrl = "smb://smb-server/fileshare/" + filename;
        SmbFile smbFile = new SmbFile(smbUrl, context);
        try (OutputStream out = smbFile.getOutputStream()) {
            fileStream.transferTo(out);
        }
    }

    public InputStream getFileAsStream(String filename) throws Exception {
        String smbUrl = "smb://smb-server/fileshare/" + filename;
        SmbFile smbFile = new SmbFile(smbUrl, context);
        return new SmbFileInputStream(smbFile);
    }
}
