;;; corkscrew.el --- For launching Clojure SLIME sessions

;; Copyright (C) 2009 Phil Hagelberg
;;
;; Author: Phil Hagelberg
;; URL: http://github.com/technomancy/corkscrew
;; Version: 0.1
;; Keywords: lisp slime repl
;; Created: 2009-06-07

;; This file is not part of GNU Emacs.

;;; Commentary:

;; A function to launch SLIME sessions for a given Clojure project.

;; TODO: more explanation.

;;; License:

;; This program is free software; you can redistribute it and/or
;; modify it under the terms of the GNU General Public License
;; as published by the Free Software Foundation; either version 3
;; of the License, or (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;;
;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to the
;; Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
;; Boston, MA 02110-1301, USA.


(when (not (functionp 'locate-dominating-file))
  ;; If you don't have this function, get it from
  ;; http://github.com/technomancy/emacs-starter-kit/raw/2138ec178290eaddbd8bb87c2c582b8a7c67a8d0/dominating-file.el
  (require 'dominating-file))

(defun corkscrew ()
  "Launch SLIME with the classpath set up for the current project."
  (interactive (list
                (ido-read-directory-name
                 "Project root: "
                 (locate-dominating-file default-directory "project.clj"))))
  (when (get-buffer "*inferior-lisp*")
    (kill-buffer "*inferior-lisp*"))
  (add-to-list 'swank-clojure-extra-vm-args
               (format "-Dclojure.compile.path=%s"
                       (expand-file-name "target/classes/" path)))
  (setq swank-clojure-binary nil
        swank-clojure-jar-path (expand-file-name "target/dependency/" path)
        swank-clojure-extra-classpaths
        (mapcar (lambda (d) (expand-file-name d path))
                '("src/" "target/classes/" "test/"))
        slime-lisp-implementations
        (cons `(clojure ,(swank-clojure-cmd) :init swank-clojure-init)
              (remove-if #'(lambda (x) (eq (car x) 'clojure))
                         slime-lisp-implementations)))
  (save-window-excursion
    (slime)))

(provide 'corkscrew)