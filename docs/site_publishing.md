# How this site is published

This site is the GitHub Pages site of the [spam-1 repo](https://github.com/Johnlon/spam-1).

## Where it lives

- The repo is called `spam-1`, so its site is automatically published at <https://johnlon.github.io/spam-1/>.
- This is true for any repo of `Johnlon`: a repo called `X` with GitHub Pages enabled is published at `https://johnlon.github.io/X/`.
- The exception is where the main site repo, [johnlon.github.io](https://github.com/Johnlon/johnlon.github.io), has a page or folder of the same name that eclipses it. So do not add a `spam-1` page or folder to that repo.

## What is published

| Source in the repo | Published at |
|--------------------|--------------|
| `README.md` | the home page, `/spam-1/` |
| `docs/x.md` | `/spam-1/x.html` |
| images and other files in `docs/` | `/spam-1/` plus the same file name |

The README is the single source for the home page. There is no separate home page to maintain.

## How it is built

- The workflow [.github/workflows/pages.yml](https://github.com/Johnlon/spam-1/blob/master/.github/workflows/pages.yml) does the publishing.
- It runs on every push to `master` that changes `README.md` or anything in `docs/`.
- It copies `README.md` to `docs/index.md`, then builds `docs/` with Jekyll and deploys it.
- During the copy it fixes the README links: the `docs/` prefix is removed, and links into `verilog/` are pointed at GitHub.
- `docs/index.md` is generated on each build and is in `.gitignore`. Do not add one to the repo.
- The theme, title and description are set in `docs/_config.yml`.
- A push takes about a minute to go live.

## Things to know

- A push that changes neither `README.md` nor `docs/` does not rebuild the site. To rebuild by hand use the Actions tab, pick "pages", then "Run workflow".
- Only README links are fixed. A page in `docs/` that links outside `docs/`, for example to `../verilog/`, works on GitHub but not on this site.
- The repo setting Settings > Pages > Source must be "GitHub Actions".
