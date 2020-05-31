import defaultSettings from '@/settings';
import i18n from '@/lang';

const title = defaultSettings.title || 'Vue Admin Template';

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    pageTitle = i18n.t(`route.${pageTitle}`);
  }

  if (pageTitle) {
    return `${pageTitle} - ${title}`;
  }
  return `${title}`;
}
