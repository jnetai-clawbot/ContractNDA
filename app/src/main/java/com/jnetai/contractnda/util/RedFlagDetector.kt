package com.jnetai.contractnda.util

/**
 * Detects red flags in contract/nda clause text.
 * Returns list of matched red flag terms/phrases.
 */
object RedFlagDetector {

    private val redFlagPatterns = listOf(
        // Liability
        Regex("\\b(unlimited\\s+liability|personal\\s+liability|joint\\s+and\\s+several)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(indemnif[yi].*against.*all.*claims|indemnif[yi].*against.*any.*claim)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(hold\\s+harmless|defend\\s+and\\s+indemnify)\\b", RegexOption.IGNORE_CASE),

        // Termination
        Regex("\\b(terminate.*without\\s+notice|terminate.*without\\s+cause|terminate.*at\\s+will|terminate.*at\\s+any\\s+time)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(termination.*for\\s+convenience|convenience\\s+termination)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(immediate\\s+termination)\\b", RegexOption.IGNORE_CASE),

        // Non-compete
        Regex("\\b(non[- ]?compete|noncompete|non[- ]?competition)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(restrict.*from\\s+compet|prohibit.*from\\s+compet)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(sole\\s+discretion|absolute\\s+discretion)\\b", RegexOption.IGNORE_CASE),

        // IP
        Regex("\\b(work\\s+product.*belong|assign.*all.*intellectual\\s+property|transfer.*all.*ip)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(pre[- ]?existing\\s+ip.*waiv|waiv.*pre[- ]?existing)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(invention.*assignment|patent.*assignment)\\b", RegexOption.IGNORE_CASE),

        // Confidentiality
        Regex("\\b(confidential.*period.*(?:\\d+)\\s+year|non[- ]?disclosure.*(?:\\d+)\\s+year)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(perpetual\\s+confidentiality|perpetual\\s+nda|perpetual\\s+non[- ]?disclosure)\\b", RegexOption.IGNORE_CASE),

        // Payment
        Regex("\\b(penalty.*interest|late\\s+fee.*(?:\\d+)%|default\\s+interest.*(?:\\d+)%)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(liquidated\\s+damages)\\b", RegexOption.IGNORE_CASE),

        // Dispute
        Regex("\\b(arbitration.*only|binding\\s+arbitration|waive.*right.*jury|jury\\s+trial.*waiver)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(class\\s+action.*waiver|waive.*class\\s+action)\\b", RegexOption.IGNORE_CASE),

        // General abusive
        Regex("\\b(as\\s+is|without\\s+warranty|no\\s+warranty\\s+express|no\\s+warranty\\s+implied)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(entire\\s+agreement|supersedes.*prior|supersedes.*previous)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(amend.*only.*written|modification.*only.*written|no\\s+oral\\s+modification)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(waive.*right|waiver.*right|relinquish.*right)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(governing\\s+law.*(?:state|province|country))\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(force\\s+majeure.*shall\\s+not|force\\s+majeure.*does\\s+not\\s+apply)\\b", RegexOption.IGNORE_CASE),

        // Auto-renewal
        Regex("\\b(auto[- ]?renew|automatic\\s+renewal|evergreen\\s+clause)\\b", RegexOption.IGNORE_CASE),

        // Assignment
        Regex("\\b(may\\s+assign.*without\\s+consent|assign.*without\\s+notice|assign.*without\\s+approval)\\b", RegexOption.IGNORE_CASE),

        // Limitation of liability
        Regex("\\b(limitation.*liability|cap.*liability|maximum.*liability)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(consequential\\s+damages.*exclud|exclud.*consequential\\s+damages)\\b", RegexOption.IGNORE_CASE),

        // Broad grants
        Regex("\\b(irrevocable.*license|perpetual.*license|worldwide.*license|exclusive.*license)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(sublicensable|sublicense|right\\s+to\\s+sublicense)\\b", RegexOption.IGNORE_CASE),

        // Penalty clauses
        Regex("\\b(penal(?:ty|ties)|forfeit|forfeiture)\\b", RegexOption.IGNORE_CASE)
    )

    fun detect(text: String): List<String> {
        val found = mutableListOf<String>()
        for (pattern in redFlagPatterns) {
            if (pattern.containsMatchIn(text)) {
                val match = pattern.find(text)
                if (match != null) {
                    found.add(match.value.replaceFirstChar { it.uppercase() })
                }
            }
        }
        return found.distinct()
    }

    fun riskScore(text: String): Int {
        val flags = detect(text)
        return (flags.size * 15).coerceAtMost(100)
    }
}