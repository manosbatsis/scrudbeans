/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require('react');

class Footer extends React.Component {
    docUrl(doc, language) {
        const baseUrl = this.props.config.baseUrl;
        const docsUrl = this.props.config.docsUrl;
        const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
        const langPart = `${language ? `${language}/` : ''}`;
        return `${baseUrl}${docsPart}${langPart}${doc}`;
    }

    pageUrl(doc, language) {
        const baseUrl = this.props.config.baseUrl;
        return baseUrl + (language ? `${language}/` : '') + doc;
    }

    render() {
        return (
            <footer className="nav-footer" id="footer">
                <section className="copyright">
                    <a href="http://manosbatsis.github.io/">{this.props.config.copyright}</a>.
                    <span> Got a remote contract? Contact me by
                    <a href="mailto:manosbatsis@gmail.com?subject=Remote Contract"> email</a> or
                    <a href="https://www.linkedin.com/in/manosbatsis"> linkedin</a>
                    </span>
                </section>
            </footer>
        );
    }
}

module.exports = Footer;
