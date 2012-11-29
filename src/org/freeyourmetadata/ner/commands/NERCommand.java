package org.freeyourmetadata.ner.commands;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONWriter;

import com.google.refine.commands.Command;

public abstract class NERCommand extends Command {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Content-Type", "application/json");
        JSONWriter writer = new JSONWriter(response.getWriter());
        try {
            get(request, writer);
        }
        catch (Exception error){
            error.printStackTrace();
            throw new ServletException(error);
        }
    }
    
    public void get(HttpServletRequest request, JSONWriter response) throws Exception {}
}
