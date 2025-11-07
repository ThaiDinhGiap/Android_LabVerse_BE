package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.service.CitationService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitationServiceImpl implements CitationService {

    // ===== Public APIs =====
    @Override
    public String toAPA(Paper p) {
        String authors = apaAuthors(p.getAuthor());
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "n.d.";
        String title = nz(p.getTitle());
        String journal = nz(p.getJournal());
        String link = safeDoiLink(p.getDoi());

        StringBuilder sb = new StringBuilder();
        if (!authors.isBlank()) sb.append(authors).append(". ");
        sb.append("(").append(year).append("). ");
        sb.append(title).append(". ");
        if (!journal.isBlank()) sb.append(journal).append(".");
        if (link != null) sb.append(" ").append(link);
        return sb.toString().trim();
    }

    @Override
    public String toMLA(Paper p) {
        String authors = mlaAuthors(p.getAuthor());
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "";
        String title = nz(p.getTitle());
        String journal = nz(p.getJournal());
        String link = safeDoiLink(p.getDoi());

        StringBuilder sb = new StringBuilder();
        if (!authors.isBlank()) sb.append(authors).append(". ");
        sb.append("\"").append(title).append(".\" ");
        if (!journal.isBlank()) sb.append(journal).append(", ");
        if (!year.isBlank()) sb.append(year).append(".");
        if (link != null) sb.append(" ").append(link);
        return sb.toString().trim();
    }

    @Override
    public String toBibTeX(Paper p) {
        String key = buildBibKey(p);
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("@article{").append(key).append(",\n")
                .append("  title = {").append(escapeBib(nz(p.getTitle()))).append("},\n")
                .append("  author = {").append(escapeBib(nz(p.getAuthor()))).append("},\n");
        if (!nz(p.getJournal()).isBlank())
            sb.append("  journal = {").append(escapeBib(p.getJournal())).append("},\n");
        if (!year.isBlank())
            sb.append("  year = {").append(year).append("},\n");
        if (!nz(p.getDoi()).isBlank())
            sb.append("  doi = {").append(escapeBib(p.getDoi())).append("},\n");
        sb.append("}");
        return sb.toString();
    }

    // ===== Helpers =====
    private String nz(String s){ return s == null ? "" : s; }

    private String safeDoiLink(String doiRaw){
        if (doiRaw == null) return null;
        String d = doiRaw.trim();
        if (d.isEmpty()) return null;
        d = d.replaceFirst("^(?i)https?://(dx\\.)?doi\\.org/", "")
                .replaceFirst("^(?i)doi:\\s*", "")
                .replace(" ", "");
        return "https://doi.org/" + d;
    }

    private String escapeBib(String s){
        return s == null ? "" : s.replace("\\","\\\\")
                .replace("{","\\{")
                .replace("}","\\}")
                .replace("\"","\\\"");
    }

    private String buildBibKey(Paper p){
        String last = safeLastName(p.getAuthor());
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "";
        if (last.isBlank()) last = "Paper";
        if (year.isBlank()) year = String.valueOf(System.currentTimeMillis());
        return (last + year).replaceAll("\\s+", "");
    }

    private String safeLastName(String authorField){
        if (authorField == null || authorField.isBlank()) return "";
        String firstAuthor = authorField.split(";")[0].trim();
        String[] parts = firstAuthor.split("\\s+");
        return parts.length == 0 ? "" : parts[parts.length - 1];
    }

    // ---- Authors formatting (tối giản, đủ dùng) ----
    private List<String> splitAuthors(String s) {
        if (s == null || s.isBlank()) return List.of();
        return Arrays.stream(s.split(";"))
                .map(String::trim)
                .filter(a -> !a.isEmpty())
                .toList();
    }

    // "Nguyen Van A" -> "Nguyen, V. A."
    private String apaName(String full) {
        String[] parts = full.trim().split("\\s+");
        if (parts.length == 0) return "";
        String last = parts[parts.length - 1];
        String initials = Arrays.stream(parts, 0, parts.length - 1)
                .map(p -> p.substring(0,1).toUpperCase() + ".")
                .collect(Collectors.joining(" "));
        return (last + (initials.isEmpty() ? "" : ", " + initials)).trim();
    }

    private String apaAuthors(String s){
        List<String> a = splitAuthors(s);
        if (a.isEmpty()) return "";
        List<String> mapped = a.stream().map(this::apaName).toList();
        if (mapped.size() == 1) return mapped.get(0);
        if (mapped.size() <= 20) {
            return String.join(", ", mapped.subList(0, mapped.size() - 1)) + ", & " + mapped.get(mapped.size() - 1);
        }
        return mapped.get(0) + " et al.";
    }

    // "Nguyen Van A" -> "Nguyen, Van A"
    private String mlaNameFirst(String full) {
        String[] parts = full.trim().split("\\s+");
        if (parts.length == 0) return "";
        String last = parts[parts.length - 1];
        String given = String.join(" ", Arrays.copyOf(parts, parts.length - 1));
        return (last + (given.isEmpty() ? "" : ", " + given)).trim();
    }

    private String mlaAuthors(String s){
        List<String> a = splitAuthors(s);
        if (a.isEmpty()) return "";
        if (a.size() == 1) return mlaNameFirst(a.get(0));
        if (a.size() == 2) return mlaNameFirst(a.get(0)) + ", and " + a.get(1);
        return mlaNameFirst(a.get(0)) + ", et al.";
    }
}
