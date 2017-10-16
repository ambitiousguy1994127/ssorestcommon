package com.idfconnect.ssorest.common.crypto;

import org.apache.commons.cli.*;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>CryptoUtil class.</p>
 *
 * @author nghia
 */
public class CryptoUtil {
    private final static String        APP_NAME   = "SSORestCryptoUtil";
    private              String[]      args       = null;
    private              Options       options    = new Options();
    private              String        gatewayUrl = null;
    private              String        action     = null;
    private              String        user       = null;
    private              String        password   = null;
    private              String        text       = null;
    private              HelpFormatter formater   = new HelpFormatter();

    /**
     * <p>Constructor for CryptoUtil.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public CryptoUtil(String[] args) {

        this.args = args;
        options.addOption("h", "help", false, "show help");
        options.addRequiredOption("g", "gateway", true, "Gateway URL");
        options.addRequiredOption("a", "action", true, "action: encrypt|decrypt");
        options.addRequiredOption("u", "user", true, "Policy Server Admin Name");
        options.addRequiredOption("p", "password", true, "Policy Server Admin Password");

    }

    /**
     * <p>parse.</p>
     */
    public void parse() {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("g")) {
                gatewayUrl = cmd.getOptionValue("g");
            } else {
                help();
            }
            if (cmd.hasOption("a")) {
                action = cmd.getOptionValue("a");
            } else {
                help();
            }
            if (cmd.hasOption("u")) {
                user = cmd.getOptionValue("u");
            } else {
                help();
            }
            if (cmd.hasOption("p")) {
                password = cmd.getOptionValue("p");
            } else {
                help();
            }
            List<String> list = cmd.getArgList();
            if (list == null || list.size() != 1) {
                throw new ParseException("empty args");
            }
            text = list.get(0);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Incorrect command");
            help();
        }
    }

    private void help() {
        formater.printHelp(APP_NAME, options, true);
        System.exit(1);
    }

    /**
     * <p>protectSecret.</p>
     */
    public void protectSecret() {
        String endpoint = gatewayUrl + "/service/gateway/protectSecret";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(endpoint);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("action", action));
        nvps.add(new BasicNameValuePair("pluginSecret", text));
        nvps.add(new BasicNameValuePair("adminName", user));
        nvps.add(new BasicNameValuePair("adminPwd", password));

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = httpclient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity.getContent() != null) {
                InputStream is = resEntity.getContent();
                String line;

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            }
            EntityUtils.consume(resEntity);
        } catch (Exception ex) {
            formater.printHelp("Error: " + ex.getMessage(), options, false);
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                //ignored
            }
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                //ignored
            }
        }

        formater.printHelp("Result: " + sb.toString(), options, false);

    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        CryptoUtil util = new CryptoUtil(args);
        util.parse();
        util.protectSecret();
    }
}
